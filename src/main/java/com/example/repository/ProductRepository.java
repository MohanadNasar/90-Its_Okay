package com.example.repository;

import com.example.model.Cart;
import com.example.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class ProductRepository extends MainRepository<Product> {
    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/products.json";
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

    public Product updateProduct(UUID productId, String newName, double newPrice){
        ArrayList<Product> products = this.getProducts();
        for (Product product : products) {
            if (product.getId() != null && product.getId().equals(productId)) {
                product.setName(newName);
                product.setPrice(newPrice);
                break;
            }
        }
        this.overrideData(products);
        return this.getProductById(productId);
    }

    public void applyDiscount(double discount, ArrayList<UUID> productIds){
        ArrayList<Product> products = this.getProducts();
        for (Product product : products) {
            if (productIds.contains(product.getId())) {
                product.setPrice(product.getPrice() - (product.getPrice() * (discount/100)));
            }
        }
        this.overrideData(products);
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