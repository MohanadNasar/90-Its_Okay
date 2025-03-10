package com.example.repository;

import com.example.model.Order;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

@Repository
public class OrderRepository extends MainRepository<Order> {
    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/orders.json";
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