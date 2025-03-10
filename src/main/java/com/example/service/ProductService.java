package com.example.service;

import com.example.model.Product;
import com.example.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class ProductService extends MainService<Product> {
    //The Dependency Injection Variables
    private final ProductRepository productRepository;
    private final OrderService orderService;


    //The Constructor with the requried variables mapping the Dependency Injection.
    public ProductService(ProductRepository productRepository, OrderService orderService) {
        this.productRepository = productRepository;
        this.orderService = orderService;
    }
    public Product addProduct(Product product){

        if (product == null || product.getId() == null) {
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
        return productRepository.getProductById(productId);
    }
    public Product updateProduct(UUID productId, String newName, double newPrice){
        if(productId == null){
            throw new IllegalArgumentException("Invalid product ID entered");
        }
        if(newPrice<0){
            throw new IllegalArgumentException("Invalid price entered");
        }
        if(newName.isEmpty()){
            throw new IllegalArgumentException("Invalid name entered");
        }
        if(getProductById(productId) == null){
            throw new IllegalArgumentException("Product not found");
        }
        return productRepository.updateProduct(productId, newName, newPrice);
    }
    public void applyDiscount(double discount, ArrayList<UUID> productIds){
        if(discount < 0){
            throw new IllegalArgumentException("Negative discount percentage");
        }
        if(productIds.isEmpty()){
            throw new IllegalArgumentException("No products provided for discount");
        }
        productRepository.applyDiscount(discount, productIds);
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
        productRepository.deleteProductById(productId);
    }


}