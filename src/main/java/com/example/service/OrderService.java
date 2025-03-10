package com.example.service;

import com.example.model.Order;
import com.example.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class OrderService extends MainService<Order> {

    //The Dependency Injection Variables
    OrderRepository orderRepository;
    //The Constructor with the requried variables mapping the Dependency Injection.
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
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
        return orderRepository.getOrderById(orderId);
    }

    public void deleteOrderById(UUID orderId) throws IllegalArgumentException{
        Order order = orderRepository.getOrderById(orderId);

        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }

        // Check if order has products
        if (order.getProducts() != null && !order.getProducts().isEmpty()) {
            throw new IllegalStateException("Cannot delete order: It contains products.");
        }

        orderRepository.deleteOrderById(orderId);
    }

}
