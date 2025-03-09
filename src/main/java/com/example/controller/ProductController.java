package com.example.controller;

import com.example.model.Product;
import org.springframework.web.bind.annotation.*;

import com.example.service.ProductService;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {
    //The Dependency Injection Variables
    private ProductService productService;

    //The Constructor with the requried variables mapping the Dependency Injection.
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/")
    public Product addProduct(@RequestBody Product product){
        return productService.addProduct(product);
    }

    @GetMapping("/")
    public ArrayList<Product> getProducts(){
        return productService.getProducts();
    }

    @GetMapping("/{productId}")
    public Product getProductById(@PathVariable UUID productId){
        return productService.getProductById(productId);
    }

    @PutMapping("/update/{productId}")
    public Product updateProduct(@PathVariable UUID productId, @RequestBody Map<String,Object> body){
        String newName = (String) body.get("name");
        double newPrice = (double) body.get("price");
        return productService.updateProduct(productId, newName, newPrice);
    }

    @PutMapping("/applyDiscount")
    public String applyDiscount(@RequestParam double discount,@RequestBody ArrayList<UUID>
            productIds){
        productService.applyDiscount(discount, productIds);
        return "Discount applied";
    }

    @DeleteMapping("/delete/{productId}")
    public String deleteProductById(@PathVariable UUID productId){
        productService.deleteProductById(productId);
        return "Product deleted";
    }




}