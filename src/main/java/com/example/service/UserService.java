package com.example.service;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.CartRepository;
import com.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService extends MainService<User>{
    //The Dependency Injection Variables
    private final UserRepository userRepository;
    private final CartService cartService;
    private final OrderService orderService;
    private final CartRepository cartRepository;

    //The Constructor with the required variables mapping the Dependency Injection.
    public UserService(UserRepository userRepository, CartService cartService, OrderService orderService, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.orderService = orderService;
        this.cartRepository = cartRepository;
    }

    public User addUser(User user){
        if (user == null || user.getName() == null || user.getName().isEmpty() ) {
            throw new IllegalArgumentException("Invalid user data");
        }
        // Check for duplicate user
        ArrayList<User> existingUsers = userRepository.getUsers();
        for (User existingUser : existingUsers) {
            if (existingUser.getId().equals(user.getId())) {
                throw new IllegalStateException("User already exists");
            }
        }
        return userRepository.addUser(user);
    }

    public ArrayList<User> getUsers(){
        return userRepository.getUsers();
    }

    public User getUserById(UUID userId){
        if (userId == null) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return userRepository.getUserById(userId);
    }

    public List<Order> getOrdersByUserId(UUID userId){
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return userRepository.getOrdersByUserId(userId);
    }

    //TODO: revisit this method
    // The method to add an order to a user. Here the user should empty his cart and calculate everything related to his order and add the new order to his list of orders.
    // It should call methods from cartservice.
    public void addOrderToUser(UUID userId){

        User user = userRepository.getUserById(userId);
        if(user == null){
            throw new IllegalArgumentException("User not found");
        }

        Cart cart = cartService.getCartByUserId(userId);

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found for user: " + userId);
        }

        if(cart.getProducts().size() <= 0){
            throw new IllegalArgumentException("Cart is empty");
        }

        double totalPrice = 0;
        for (Product product : cart.getProducts()) {
            totalPrice += product.getPrice();
        }


        // Create a new order
        Order order = new Order(userId,totalPrice,cart.getProducts());
        orderService.addOrder(order);

        //Add the order to the user
        userRepository.addOrderToUser(userId, order);

        // Empty the cart of the user
        this.emptyCart(userId);
    }

    //TODO: revisit this method
    public void emptyCart(UUID userId){
        Cart cart = cartService.getCartByUserId(userId);

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found for user: " + userId);
        }

        if(!cart.getProducts().isEmpty()) {
            Cart emptyCart = cartService.getCartByUserId(userId);
            cartService.deleteCartById(emptyCart.getId());
            cartService.addCart(new Cart(emptyCart.getId(),userId,new ArrayList<>()));
        }
    }
    public void removeOrderFromUser(UUID userId, UUID orderId){
        User user = userRepository.getUserById(userId);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        List<Order> userOrders = userRepository.getOrdersByUserId(userId);

        boolean orderExists = userOrders.stream()
                .anyMatch(order -> order.getId().equals(orderId));

        if (!orderExists) {
            throw new IllegalArgumentException("Order not found for this user");
        }

        //remove the order from the order repository
        userRepository.removeOrderFromUser(userId, orderId);
        orderService.deleteOrderById(orderId);

    }
    public void deleteUserById(UUID userId){
        User user = userRepository.getUserById(userId);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Check if user has active orders
        if (!userRepository.getOrdersByUserId(userId).isEmpty()) {
            throw new IllegalStateException("Cannot delete user with active orders");
        }

        //Should delete any carts associated with the user
        try{
            Cart cart = cartService.getCartByUserId(userId);
            if(cart != null){
                cartService.deleteCartById(cart.getId());
            }
        }
        catch (Exception e){
            userRepository.deleteUserById(userId);
        }

        userRepository.deleteUserById(userId);



    }









}