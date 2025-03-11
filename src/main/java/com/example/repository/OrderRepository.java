package com.example.repository;

import com.example.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

@Repository
public class OrderRepository extends MainRepository<Order> {

    @Value("${spring.application.orderDataPath}")
    private String orderDataPath;

    @Override
    protected String getDataPath() {
        return orderDataPath;
    }

    @Override
    protected Class<Order[]> getArrayType() {
        return Order[].class;
    }

    public OrderRepository() {
    }

    public Order addOrder(Order order){
        this.save(order);
        return order;
    }

    public ArrayList<Order> getOrders(){
        return this.findAll();
    }

    public Order getOrderById(UUID orderId){
        for (Order order : this.getOrders()) {
            if (order.getId() != null && order.getId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }

    public void deleteOrderById(UUID orderId){
        ArrayList<Order> orders = this.getOrders();
        for (Order order : orders) {
            if (order.getId() != null && order.getId().equals(orderId)) {
                orders.remove(order);
                break;
            }
        }
        this.overrideData(orders);
    }

    public void clearOrders() {
        ArrayList<Order> orders = this.findAll();
        orders.clear();
        this.overrideData(orders);
    }

}