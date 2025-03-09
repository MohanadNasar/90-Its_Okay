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
    void getUserById_whenUserNotFound_shouldReturnNull() {
        UUID nonExistentUserId = UUID.randomUUID();
        User user = userService.getUserById(nonExistentUserId);
        assertNull(user, "If the user does not exist, the method should return null instead of throwing an exception.");
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
    void addOrderToUser_whenCartNotFound_shouldThrowException() {
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
        User deletedUser = userService.getUserById(userId);
        assertNull(deletedUser, "After deletion, getUserById should return null instead of throwing an exception.");
    }

    @Tag("user")
    @Test
    void deleteUser_whenUserHasOrders_shouldThrowException() {
        userRepository.addOrderToUser(userId, testOrder);
        assertThrows(IllegalStateException.class, () -> userService.deleteUserById(userId),
                "Deleting a user with orders should throw an exception");
    }

    @Tag("user")
    @Test
    void deleteUser_whenUserNotFound_shouldThrowException() {
        UUID nonExistentUserId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUserById(nonExistentUserId),
                "Deleting a user with orders should throw an exception");
    }

    // ------------------------ Cart Tests -------------------------

    // 1) Add Cart Tests
    @Tag("cart")
    @Test
    void addCart_withValidInput_shouldReturnSameCartId() {
        Cart createdCart = cartService.addCart(testCart);
        assertEquals(testCart.getId(), createdCart.getId(), "Cart ID should match");
    }

    @Tag("cart")
    @Test
    void addCart_withInvalidData_shouldThrowException() {
        Cart invalidCart = new Cart(); // Missing required fields
        assertThrows(IllegalArgumentException.class, () -> cartService.addCart(invalidCart),
                "Should throw an exception for invalid cart data");
    }

    @Tag("cart")
    @Test
    void addCart_withDuplicateCart_shouldThrowException() {
        cartService.addCart(testCart);
        assertThrows(IllegalStateException.class, () -> cartService.addCart(testCart),
                "Should throw an exception for duplicate cart");
    }

    // 2) Get All Carts Tests
    @Tag("cart")
    @Test
    void getCarts_shouldReturnListOfCarts() {
        List<Cart> carts = cartService.getCarts();
        assertNotNull(carts, "Carts list should not be null");
    }

    @Tag("cart")
    @Test
    void getCarts_whenNoCarts_shouldReturnEmptyList() {
        List<Cart> carts = cartService.getCarts();
        assertEquals(0, carts.size(), "Should return an empty list if no carts exist");
    }

    @Tag("cart")
    @Test
    void getCarts_whenMultipleCartsExist_shouldReturnCorrectSize() {
        cartService.addCart(testCart);
        List<Cart> carts = cartService.getCarts();
        assertEquals(1, carts.size(), "Cart list size should match the number of carts added");
    }

    // 3) Get Cart By ID Tests
    @Tag("cart")
    @Test
    void getCartById_withValidId_shouldReturnCart() {
        cartService.addCart(testCart);
        Cart retrievedCart = cartService.getCartById(testCart.getId());
        assertNotNull(retrievedCart, "Cart should be found");
    }

    @Tag("cart")
    @Test
    void getCartById_withInvalidId_shouldThrowException() {
        UUID nonExistentCartId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> cartService.getCartById(nonExistentCartId),
                "Should throw an exception if cart is not found");
    }

    @Tag("cart")
    @Test
    void getCartById_withNullId_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> cartService.getCartById(null),
                "Should throw an exception if cart ID is null");
    }

    // 4) Get Cart By User ID Tests
    @Tag("cart")
    @Test
    void getCartByUserId_withValidUser_shouldReturnCart() {
        cartService.addCart(testCart);
        Cart retrievedCart = cartService.getCartByUserId(userId);
        assertNotNull(retrievedCart, "Cart should be found");
    }

    @Tag("cart")
    @Test
    void getCartByUserId_whenUserNotFound_shouldThrowException() {
        UUID nonExistentUserId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> cartService.getCartByUserId(nonExistentUserId),
                "Should throw an exception if user is not found");
    }

    @Tag("cart")
    @Test
    void getCartByUserId_withNullUserId_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> cartService.getCartByUserId(null),
                "Should throw an exception if user ID is null");
    }

    // 5) Add Product to Cart Tests
    @Tag("cart")
    @Test
    void addProductToCart_withValidInput_shouldAddProduct() {
        cartService.addCart(testCart);
        cartService.addProductToCart(testCart.getId(), testProduct);
        assertTrue(testCart.getProducts().contains(testProduct), "Product should be added to cart");
    }

    @Tag("cart")
    @Test
    void addProductToCart_whenCartNotFound_shouldThrowException() {
        UUID nonExistentCartId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> cartService.addProductToCart(nonExistentCartId, testProduct),
                "Should throw an exception if cart is not found");
    }

    @Tag("cart")
    @Test
    void addProductToCart_withNullProduct_shouldThrowException() {
        cartService.addCart(testCart);
        assertThrows(IllegalArgumentException.class, () -> cartService.addProductToCart(testCart.getId(), null),
                "Should throw an exception if product is null");
    }

    // 6) Delete Product from Cart Tests
    @Tag("cart")
    @Test
    void deleteProductFromCart_withValidInput_shouldRemoveProduct() {
        cartService.addCart(testCart);
        cartService.addProductToCart(testCart.getId(), testProduct);
        cartService.deleteProductFromCart(testCart.getId(), testProduct);
        assertFalse(testCart.getProducts().contains(testProduct), "Product should be removed from cart");
    }

    @Tag("cart")
    @Test
    void deleteProductFromCart_whenCartNotFound_shouldThrowException() {
        UUID nonExistentCartId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> cartService.deleteProductFromCart(nonExistentCartId, testProduct),
                "Should throw an exception if cart is not found");
    }

    @Tag("cart")
    @Test
    void deleteProductFromCart_whenProductNotFound_shouldThrowException() {
        cartService.addCart(testCart);
        assertThrows(IllegalArgumentException.class, () -> cartService.deleteProductFromCart(testCart.getId(), new Product(UUID.randomUUID(), "Non-existent Product", 10.0)),
                "Should throw an exception if product is not found in cart");
    }

    // 7) Delete Cart Tests
    @Tag("cart")
    @Test
    void deleteCart_withValidId_shouldDeleteSuccessfully() {
        cartService.addCart(testCart);
        cartService.deleteCartById(testCart.getId());
        assertThrows(IllegalArgumentException.class, () -> cartService.getCartById(testCart.getId()),
                "Cart should be deleted and throw an exception when accessed");
    }

    @Tag("cart")
    @Test
    void deleteCart_whenCartNotFound_shouldThrowException() {
        UUID nonExistentCartId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> cartService.deleteCartById(nonExistentCartId),
                "Should throw an exception if cart is not found");
    }

    @Tag("cart")
    @Test
    void deleteCart_whenCartHasProducts_shouldThrowException() {
        testCart.setProducts(List.of(testProduct));
        cartService.addCart(testCart);
        assertThrows(IllegalStateException.class, () -> cartService.deleteCartById(testCart.getId()),
                "Should throw an exception if cart has products");
    }
}