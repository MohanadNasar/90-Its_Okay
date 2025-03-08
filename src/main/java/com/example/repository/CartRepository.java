package com.example.repository;

import com.example.model.Cart;
import com.example.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class CartRepository extends MainRepository<Cart> {
    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/carts.json";
    }

    @Override
    protected Class<Cart[]> getArrayType() {
        return Cart[].class;
    }

    public CartRepository() {
    }

    public Cart addCart(Cart cart){
        this.save(cart);
        return cart;
    }
    public ArrayList<Cart> getCarts(){
        return this.findAll();
    }
    public Cart getCartById(UUID cartId){
        for (Cart cart : this.getCarts()) {
            if (cart.getId().equals(cartId)) {
                return cart;
            }
        }
        return null;
    }

    public Cart getCartByUserId(UUID userId){
        for (Cart cart : this.getCarts()) {
            if (cart.getUserId().equals(userId)) {
                return cart;
            }
        }
        return null;
    }

    public void addProductToCart(UUID cartId, Product product){
        ArrayList<Cart> carts = this.getCarts();
        for (Cart cart : carts) {
            if (cart.getId().equals(cartId)) {
                cart.getProducts().add(product);
                break;
            }
        }
        this.overrideData(carts);
    }
    public void deleteProductFromCart(UUID cartId, Product product){
        ArrayList<Cart> carts = this.getCarts();
        for (Cart cart : carts) {
            if (cart.getId().equals(cartId)) {
                cart.getProducts().remove(product);
                break;
            }
        }
        this.overrideData(carts);
    }
    public void deleteCartById(UUID cartId){
        ArrayList<Cart> carts = this.getCarts();
        for (Cart cart : carts) {
            if (cart.getId().equals(cartId)) {
                carts.remove(cart);
                break;
            }
        }
        this.overrideData(carts);
    }


}