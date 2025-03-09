package com.example.service;

import com.example.model.Order;
import com.example.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class OrderService extends MainService<Order> {

    //The Dependency Injection Variables
    OrderRepository orderRepository;
    //The Constructor with the requried variables mapping the Dependency Injection.
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    public Order addOrder(Order order){
        return orderRepository.addOrder(order);
    }

    public ArrayList<Order> getOrders(){
        return orderRepository.getOrders();
    }

    public Order getOrderById(UUID orderId){
        return orderRepository.getOrderById(orderId);
    }

    public void deleteOrderById(UUID orderId) throws IllegalArgumentException{

        if (orderRepository.getOrderById(orderId) == null) {
            throw new IllegalArgumentException("Order not found");
        }
        orderRepository.deleteOrderById(orderId);
    }

}
