package com.example.repository;

import com.example.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

@Repository
public class ProductRepository extends MainRepository<Product> {

    @Value("${spring.application.productDataPath}")
    private String productDataPath;

    @Override
    protected String getDataPath() {
        return productDataPath;
    }

    @Override
    protected Class<Product[]> getArrayType() {
        return Product[].class;
    }

    public ProductRepository() {}

    public Product addProduct(Product product){
        this.save(product);
        return product;
    }
    public ArrayList<Product> getProducts(){
        return this.findAll();
    }

    public Product getProductById(UUID productId){
        for (Product product : this.getProducts()) {
            if (product.getId() != null && product.getId().equals(productId)) {
                return product;
            }
        }
        return null;
    }

    public Product updateProduct(UUID productId, String newName, double newPrice) throws Exception {
        Product product = getProductById(productId);

        if (newName == null || newName.isEmpty()) {
            throw new Exception("Please write a name");
        }

        if (newPrice < 0) {
            throw new Exception("Price should be a negative value");
        }

        // Update the product
        product.setName(newName);
        product.setPrice(newPrice);

        ArrayList<Product> products = findAll();
        products.remove(product);
        products.add(product);
        saveAll(products);
        return product;
    }

    public void applyDiscount(double discount, ArrayList<UUID> productIds){
        ArrayList<Product> products = findAll();
        for (UUID productId : productIds) {
            Product product = getProductById(productId);
            double newPrice = product.getPrice() * (1 - discount / 100);
            product.setPrice(newPrice);
            products.remove(product);
            products.add(product);
        }

        saveAll(products);
    }

    public void deleteProductById(UUID productId){
        ArrayList<Product> products = this.getProducts();
        for (Product product : products) {
            if (product.getId() != null && product.getId().equals(productId)) {
                products.remove(product);
                break;
            }
        }
        this.overrideData(products);
    }

    public void clearProducts() {
        ArrayList<Product> products = this.findAll();
        products.clear();
        this.overrideData(products);
    }

}