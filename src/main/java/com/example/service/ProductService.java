package com.example.service;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.repository.CartRepository;
import com.example.repository.ProductRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.rest.webmvc.support.ExcerptProjector;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class ProductService extends MainService<Product> {
    //The Dependency Injection Variables
    private final ProductRepository productRepository;
    private final OrderService orderService;
    private final CartRepository cartRepository;


    //The Constructor with the requried variables mapping the Dependency Injection.
    public ProductService(ProductRepository productRepository, @Lazy OrderService orderService, @Lazy CartRepository cartRepository) {
        this.productRepository = productRepository;
        this.orderService = orderService;
        this.cartRepository = cartRepository;
    }
    public Product addProduct(Product product){

        if (product == null || product.getName() == null || product.getName().isEmpty()) {
            throw new IllegalArgumentException("Product not found");
        }

        ArrayList<Product> existingProducts = productRepository.getProducts();
        for (Product existingProduct : existingProducts) {
            if (existingProduct.getId().equals(product.getId())) {
                throw new IllegalStateException("Product already exists");
            }
        }

        return productRepository.addProduct(product);
    }
    public ArrayList<Product> getProducts(){
        ArrayList<Product> products = productRepository.getProducts();
        return (products != null) ? products : new ArrayList<>();
    }
    public Product getProductById(UUID productId){

        if(productId == null){
            throw  new IllegalArgumentException("Product ID is null");
        }

        if(productRepository.getProductById(productId) == null){
            throw new IllegalArgumentException("Product not found");
        }

        return productRepository.getProductById(productId);
    }
    public Product updateProduct(UUID productId, String newName, double newPrice) throws Exception {
        Product newProduct = productRepository.updateProduct(productId, newName, newPrice);

        // Update the product in the cart
        for (UUID cartId : cartRepository.getCarts().stream().map(cart -> cart.getId()).toList()) {
            Product oldProduct = productRepository.getProductById(productId);
            cartRepository.deleteProductFromCart(cartId, oldProduct);
            cartRepository.addProductToCart(cartId, newProduct);
        }

        return newProduct;
    }
    public void applyDiscount(double discount, ArrayList<UUID> productIds){
        if(discount < 0 || discount > 100){
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        if(productIds.isEmpty()){
            throw new IllegalArgumentException("No products provided for discount");
        }
        if(productIds.stream().anyMatch(id -> getProductById(id) == null)){
            throw new IllegalArgumentException("Invalid product ID entered");
        }

        //iterate over carts and for each cart for each product inside it with the same id as my updated product delete it and readd it using the updated product
        // Iterate over all carts and update the products inside them
        productRepository.applyDiscount(discount, productIds);

        // Update the products in all carts with the new discounted prices
        for (UUID cartId : cartRepository.getCarts().stream().map(cart -> cart.getId()).toList()) {
            for (UUID productId : productIds) {
                Product updatedProduct = productRepository.getProductById(productId);
                cartRepository.deleteProductFromCart(cartId, updatedProduct);
                cartRepository.addProductToCart(cartId, updatedProduct);
            }
        }
    }
    public void deleteProductById(UUID productId){

        Product product = getProductById(productId);

        if(product == null){
            throw new IllegalArgumentException();
        }
        // Check if product is linked to orders
        boolean isProductInOrders = orderService.getOrders().stream()
                .anyMatch(order -> order.getProducts().stream().anyMatch(p -> p.getId().equals(productId)));

        if (isProductInOrders) {
            throw new IllegalStateException("Cannot delete product linked to existing orders");
        }

        //delete the product from the cart
        for (UUID cartId : cartRepository.getCarts().stream().map(cart -> cart.getId()).toList()) {
            cartRepository.deleteProductFromCart(cartId, product);
        }

        productRepository.deleteProductById(productId);
    }


}