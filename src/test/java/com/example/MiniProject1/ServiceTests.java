package com.example.MiniProject1;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.CartService;
import com.example.service.OrderService;
import com.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    private UUID userId;
    private User testUser;
    private Cart testCart;
    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Test User");

        testCart = new Cart();
        testCart.setId(UUID.randomUUID());
        testCart.setUserId(userId);
        testCart.setProducts(List.of());

        testProduct = new Product(UUID.randomUUID(), "Test Product", 10.0);
        testOrder = new Order(UUID.randomUUID(), userId, 50.0, List.of(testProduct));

        userRepository.addUser(testUser);
    }

    // ------------------------ User Tests -------------------------

    // 1) Add New User Tests
    @Tag("user")
    @Test
    void addUser_withValidInput_shouldReturnSameName() {
        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setName("John Doe");
        User createdUser = userService.addUser(newUser);
        assertEquals(newUser.getName(), createdUser.getName(), "User name should match");
    }

    @Tag("user")
    @Test
    void addUser_withInvalidData_shouldThrowException() {
        User newUser = new User();  // Missing required fields
        assertThrows(IllegalArgumentException.class, () -> userService.addUser(newUser));
    }

    @Tag("user")
    @Test
    void addUser_withDuplicateUser_shouldThrowException() {
        assertThrows(IllegalStateException.class, () -> userService.addUser(testUser));
    }

    // 2) Get Users Tests
    @Tag("user")
    @Test
    void getUsers_shouldReturnListOfUsers() {
        ArrayList<User> users = userService.getUsers();
        assertFalse(users.isEmpty(), "Users list should not be empty");
    }

    @Tag("user")
    @Test
    void getUsers_whenNoUsers_shouldReturnEmptyList() {
        userRepository.clearUsers();
        ArrayList<User> users = userService.getUsers();
        assertEquals(0, users.size());
    }

    @Tag("user")
    @Test
    void getUsers_shouldContainSpecificUser() {
        ArrayList<User> users = userService.getUsers();
        assertTrue(users.stream().anyMatch(u -> u.getId().equals(userId)), "User should be present in list");
    }

    // 3) Get User By ID Tests
    @Tag("user")
    @Test
    void getUserById_whenUserExists_shouldReturnUser() {
        User foundUser = userService.getUserById(userId);
        assertNotNull(foundUser);
    }

    @Tag("user")
    @Test
    void getUserById_whenUserNotFound_shouldThrowException() {
        UUID nonExistentUserId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(nonExistentUserId));
    }

    @Tag("user")
    @Test
    void getUserById_shouldReturnCorrectUser() {
        User foundUser = userService.getUserById(userId);
        assertEquals(testUser.getId(), foundUser.getId());
    }

    // 4) Get User’s Orders Tests
    @Tag("user")
    @Test
    void getOrdersByUserId_whenOrdersExist_shouldReturnOrders() {
        userRepository.addOrderToUser(userId, testOrder);
        List<Order> orders = userService.getOrdersByUserId(userId);
        assertFalse(orders.isEmpty());
    }

    @Tag("user")
    @Test
    void getOrdersByUserId_whenNoOrders_shouldReturnEmptyList() {
        List<Order> orders = userService.getOrdersByUserId(userId);
        assertTrue(orders.isEmpty());
    }

    @Tag("user")
    @Test
    void getOrdersByUserId_whenUserNotFound_shouldThrowException() {
        UUID nonExistentUserId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> userService.getOrdersByUserId(nonExistentUserId));
    }

    // 5) Add a New Order Tests
    @Tag("user")
    @Test
    void addOrderToUser_whenValidCart_shouldAddOrder() {
        cartService.addCart(testCart);
        userService.addOrderToUser(userId);
        assertFalse(userService.getOrdersByUserId(userId).isEmpty());
    }

    @Tag("user")
    @Test
    void addOrderToUser_whenCartEmpty_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> userService.addOrderToUser(userId));
    }

    @Tag("user")
    @Test
    void addOrderToUser_whenUserNotFound_shouldThrowException() {
        UUID nonExistentUserId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> userService.addOrderToUser(nonExistentUserId));
    }

    // 6) Empty Cart Tests
    @Tag("user")
    @Test
    void emptyCart_whenCartExists_shouldClearProducts() {
        cartService.addCart(testCart);
        userService.emptyCart(userId);
        assertTrue(cartService.getCartByUserId(userId).getProducts().isEmpty());
    }

    @Tag("user")
    @Test
    void emptyCart_whenCartAlreadyEmpty_shouldNotFail() {
        Cart emptyCart = new Cart(UUID.randomUUID(), userId, new ArrayList<>());
        cartService.addCart(emptyCart);
        assertDoesNotThrow(() -> userService.emptyCart(userId),
                "Should not throw an exception if the cart is empty but exists");
    }


    @Tag("user")
    @Test
    void emptyCart_whenCartNotFound_shouldThrowException() {
        UUID nonExistentUserId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> userService.emptyCart(nonExistentUserId),
                "Should throw an exception if cart does not exist");
    }

    // 7) Remove Order Tests
    @Tag("user")
    @Test
    void removeOrderFromUser_whenOrderExists_shouldRemoveOrder() {
        userRepository.addOrderToUser(userId, testOrder);
        userService.removeOrderFromUser(userId, testOrder.getId());
        assertFalse(userService.getOrdersByUserId(userId).contains(testOrder));
    }

    @Tag("user")
    @Test
    void removeOrderFromUser_whenOrderNotFound_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> userService.removeOrderFromUser(userId, UUID.randomUUID()));
    }

    @Tag("user")
    @Test
    void removeOrderFromUser_whenUserNotFound_shouldThrowException() {
        UUID nonExistentUserId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> userService.removeOrderFromUser(nonExistentUserId, testOrder.getId()));
    }

    // 8) Delete User Tests
    @Tag("user")
    @Test
    void deleteUser_whenUserExists_shouldDeleteSuccessfully() {
        userService.deleteUserById(userId);
        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(userId));
    }

    @Tag("user")
    @Test
    void deleteUser_whenUserHasOrders_shouldThrowException() {
        userRepository.addOrderToUser(userId, testOrder);
        assertThrows(IllegalStateException.class, () -> userService.deleteUserById(userId));
    }

    @Tag("user")
    @Test
    void deleteUser_whenUserNotFound_shouldThrowException() {
        UUID nonExistentUserId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUserById(nonExistentUserId));
    }
}