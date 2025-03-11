package com.example.service;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class CartService extends MainService<Cart>{
    //The Dependency Injection Variables
    private final CartRepository cartRepository;

    //The Constructor with the requried variables mapping the Dependency Injection.
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart addCart(Cart cart){

        if (cart == null || cart.getUserId() == null) {
            throw new IllegalArgumentException("Cart not found");
        }

        ArrayList<Cart> existingCarts = cartRepository.getCarts();
        for (Cart existingCart : existingCarts) {
            if (existingCart.getId().equals(cart.getId())) {
                throw new IllegalStateException("Cart already exists");
            }
        }

        return cartRepository.addCart(cart);
    }

    public ArrayList<Cart> getCarts(){
        return cartRepository.getCarts();
    }

    public Cart getCartById(UUID cartId){
        if(cartId == null){
            throw new IllegalArgumentException("Cart ID is null");
        }
        if(cartRepository.getCartById(cartId) == null){
            throw new IllegalArgumentException("Cart not found");
        }
        return cartRepository.getCartById(cartId);
    }

    public Cart getCartByUserId(UUID userId){
        if(userId == null){
            throw new IllegalArgumentException("User ID is null");
        }

        return cartRepository.getCartByUserId(userId);
    }

    public void addProductToCart(UUID cartId, Product product){
        Cart cart = this.getCartById(cartId);
        if(cart == null){
            throw new IllegalArgumentException("Cart not found");
        }
        if(product == null){
            throw new IllegalArgumentException("Product not found");
        }

        cartRepository.addProductToCart(cartId, product);
    }

    public void deleteProductFromCart(UUID cartId, Product product){

        Cart cart = this.getCartById(cartId);

        if(cart == null){
            throw new IllegalArgumentException("Cart not found");
        }

        if(!cart.getProducts().contains(product)){
            throw new IllegalArgumentException("Product is not found in cart");
        }

        cartRepository.deleteProductFromCart(cartId, product);
    }

    public void deleteCartById(UUID cartId){
        Cart cart = getCartById(cartId);
        if(cart == null){
            throw new IllegalArgumentException("Cart not found");
        }

        cartRepository.deleteCartById(cartId);
    }

}
