package com.example.controller;

import com.example.model.Cart;
import com.example.model.Product;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.service.CartService;
import com.example.service.ProductService;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {
    //The Dependency Injection Variables
    private CartService cartService;


//The Constructor with the requried variables mapping the Dependency Injection.
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/")
    public Cart addCart(@RequestBody Cart cart){
        return cartService.addCart(cart);
    }

    @GetMapping("/")
    public ArrayList<Cart> getCarts(){
        return cartService.getCarts();
    }

    @GetMapping("/{cartId}")
    public Cart getCartById(@PathVariable UUID cartId){
        return cartService.getCartById(cartId);
    }

    @PutMapping("/addProduct/{cartId}")
    public String addProductToCart(@PathVariable UUID cartId, @RequestBody Product product){
        cartService.addProductToCart(cartId, product);
        return "Product added to cart";
    }

    @DeleteMapping("/delete/{cartId}")
    public String deleteCartById(@PathVariable UUID cartId){
        cartService.deleteCartById(cartId);
        return "Cart deleted successfully";
    }


}