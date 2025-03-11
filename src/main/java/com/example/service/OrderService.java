package com.example.service;

import com.example.model.Order;
import com.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class OrderService extends MainService<Order> {

    //The Dependency Injection Variables
    private final UserService userService;

    OrderRepository orderRepository;


    //The Constructor with the requried variables mapping the Dependency Injection.
    public OrderService(OrderRepository orderRepository, @Lazy UserService userService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
    }
    public Order addOrder(Order order){
        if (order == null || order.getId() == null) {
            throw new IllegalArgumentException("Order not found");
        }

        ArrayList<Order> existingOrders = orderRepository.getOrders();
        for (Order existingOrder : existingOrders) {
            if (existingOrder.getId().equals(order.getId())) {
                throw new IllegalStateException("Order already exists");
            }
        }
        return orderRepository.addOrder(order);
    }

    public ArrayList<Order> getOrders(){
        return orderRepository.getOrders();
    }

    public Order getOrderById(UUID orderId){
        if(orderId == null){
            throw  new IllegalArgumentException("Order ID is null");
        }
        if(orderRepository.getOrderById(orderId) == null){
            throw new IllegalArgumentException("Order not found");
        }
        return orderRepository.getOrderById(orderId);
    }

    public void deleteOrderById(UUID orderId) throws IllegalArgumentException{
        Order order = orderRepository.getOrderById(orderId);

        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }

        // Delete the order from the arraylist orders in the user
        try{
            userService.removeOrderFromUser(order.getUserId(), orderId);

        }
        catch(Exception e){
            orderRepository.deleteOrderById(orderId);
        }

        orderRepository.deleteOrderById(orderId);
    }

}
