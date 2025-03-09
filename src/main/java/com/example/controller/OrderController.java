package com.example.controller;

import com.example.model.Order;
import com.example.model.User;
import com.example.service.CartService;
import com.example.service.ProductService;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/order")
public class OrderController {
    //The Dependency Injection Variables
    private OrderController orderController;
    //The Constructor with the requried variables mapping the Dependency Injection.
    public OrderController(OrderController orderController) {
        this.orderController = orderController;
    }

    @PostMapping("/")
    public void addOrder(@RequestBody Order order){
        orderController.addOrder(order);
    }

    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable UUID orderId){
        return orderController.getOrderById(orderId);
    }

    @GetMapping("/")
    public ArrayList<Order> getOrders(){
        return orderController.getOrders();
    }

    @DeleteMapping("/delete/{orderId}")
    public String deleteOrderById(@PathVariable UUID orderId){
        orderController.deleteOrderById(orderId);
        return "Order deleted";
    }



}