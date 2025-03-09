package com.example.service;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class UserService extends MainService<User>{
    //The Dependency Injection Variables
    private final UserRepository userRepository;
    private final CartService cartService;

    //The Constructor with the required variables mapping the Dependency Injection.
    public UserService(UserRepository userRepository, CartService cartService) {
        this.userRepository = userRepository;
        this.cartService = cartService;

    }

    public User addUser(User user){
        return userRepository.addUser(user);
    }

    public ArrayList<User> getUsers(){
        return userRepository.getUsers();
    }

    public User getUserById(UUID userId){
        return userRepository.getUserById(userId);
    }

    public List<Order> getOrdersByUserId(UUID userId){
        return userRepository.getOrdersByUserId(userId);
    }

    //TODO: revisit this method
    // The method to add an order to a user. Here the user should empty his cart and calculate everything related to his order and add the new order to his list of orders.
    // It should call methods from cartservice.
    public void addOrderToUser(UUID userId){
        // Get the user by his id
        User user = userRepository.getUserById(userId);
        // Get the cart of the user
        Cart cart = cartService.getCartByUserId(userId);
        // Create a new order
         Order order = new Order(userId,cart.getProducts(),cart.getTotalPrice());
         //Add the order to the user
         userRepository.addOrderToUser(userId, order);
        // Empty the cart of the user
        //cartService.deleteCartById(cart.getId());
        this.emptyCart(userId);
    }

    //TODO: revisit this method
    public void emptyCart(UUID userId){
        Cart cart = cartService.getCartByUserId(userId);
        cart.getProducts().clear();
    }
    public void removeOrderFromUser(UUID userId, UUID orderId){
        userRepository.removeOrderFromUser(userId, orderId);
    }
    public void deleteUserById(UUID userId){
        userRepository.deleteUserById(userId);
    }









}