package com.example.repository;

import com.example.model.Order;
import com.example.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class UserRepository extends MainRepository<User>{
    public UserRepository() {
    }
    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/users.json";
    }

    @Override
    protected Class<User[]> getArrayType() {
        return User[].class;
    }

    public ArrayList<User> getUsers() {
        return this.findAll();
    }

    public User getUserById(UUID userId) {
        for (User user : this.getUsers()) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public User addUser(User user) {
        this.save(user);
        return user;
    }

    public List<Order> getOrdersByUserId(UUID userId) {
        return this.getUserById(userId).getOrders();
    }

    public void addOrderToUser(UUID userId, Order order) {
        ArrayList<User> users = findAll();
        for (User user : users) {
            if (user.getId().equals(userId)) {
                user.getOrders().add(order);
                break;
            }
        }
        overrideData(users);
    }

    public void removeOrderFromUser(UUID userId, UUID orderId) {
        ArrayList<User> users = findAll();
        for (User user : users) {
            if (user.getId().equals(userId)) {
                for (Order order : user.getOrders()) {
                    if (order.getId().equals(orderId)) {
                        user.getOrders().remove(order);
                        break;
                    }
                }
                break;
            }
        }
        overrideData(users);
    }


    public void saveUser(User user) {
        this.save(user);
    }

    public void deleteUserById(UUID userId) {
        ArrayList<User> users = this.getUsers();
        for (User user : users) {
            if (user.getId().equals(userId)) {
                users.remove(user);
                break;
            }
        }
        this.overrideData(users);
    }



}
