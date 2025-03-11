package com.example.MiniProject1;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.CartRepository;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import com.example.repository.UserRepository;
import com.example.service.CartService;
import com.example.service.OrderService;
import com.example.service.ProductService;
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
    private CartRepository cartRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

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
        assertNull(user, "If the user does not exist, the method should return null.");
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
        assertNull(deletedUser, "After deletion, getUserById should return null.");
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
        cartRepository.clearCarts();
        List<Cart> carts = cartService.getCarts();
        assertEquals(0, carts.size(), "Should return an empty list if no carts exist");
    }

    @Tag("cart")
    @Test
    void getCarts_whenMultipleCartsExist_shouldReturnCorrectSize() {
        int size = cartService.getCarts().size();

        Cart secondCart = new Cart();
        secondCart.setId(UUID.randomUUID());
        secondCart.setUserId(UUID.randomUUID());

        cartService.addCart(testCart);
        cartService.addCart(secondCart);

        List<Cart> carts = cartService.getCarts();
        assertEquals(2, carts.size() - size, "Cart list size should match the number of carts added.");
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
    void getCartById_withInvalidId_shouldReturnNull() {
        UUID nonExistentCartId = UUID.randomUUID();
        Cart cart = cartService.getCartById(nonExistentCartId);
        assertNull(cart, "If the cart does not exist, the method should return null.");
    }

    @Tag("cart")
    @Test
    void getCartById_withNullId_shouldReturnNull() {
        Cart cart = cartService.getCartById(null);
        assertNull(cart, "If the card ID is null, the method should return null.");
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
    void getCartByUserId_whenUserNotFound_shouldReturnNull() {
        UUID nonExistentUserId = UUID.randomUUID();
        Cart cart = cartService.getCartByUserId(nonExistentUserId);
        assertNull(cart, "If the user is nonexistent, the method should return null.");
    }

    @Tag("cart")
    @Test
    void getCartByUserId_withNullUserId_shouldReturnNull() {
        Cart cart = cartService.getCartByUserId(null);
        assertNull(cart, "If the user ID is null, the method should return null.");
    }

    // 5) Add Product to Cart Tests
    @Tag("cart")
    @Test
    void addProductToCart_withValidInput_shouldAddProduct() {
        cartService.addCart(testCart);
        cartService.addProductToCart(testCart.getId(), testProduct);
        Cart updatedCart = cartService.getCartById(testCart.getId());
        assertNotNull(updatedCart, "Cart should exist after adding a product.");
        assertTrue(updatedCart.getProducts().contains(testProduct), "Product should be added to cart.");    }

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
        Cart cart = cartService.getCartById(testCart.getId());
        assertNull(cart, "Cart should be deleted, the method should return null.");
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

    // ------------------------ Product Tests -------------------------

    // 1) Add Product Tests
    @Tag("product")
    @Test
    void addProduct_withValidInput_shouldReturnSameProductId() {
        Product createdProduct = productService.addProduct(testProduct);
        assertEquals(testProduct.getId(), createdProduct.getId(), "Product ID should match");
    }

    @Tag("product")
    @Test
    void addProduct_withInvalidData_shouldThrowException() {
        Product invalidProduct = new Product(); // Missing required fields
        assertThrows(IllegalArgumentException.class, () -> productService.addProduct(invalidProduct),
                "Should throw an exception for invalid product data");
    }

    @Tag("product")
    @Test
    void addProduct_withDuplicateProduct_shouldThrowException() {
        productService.addProduct(testProduct);
        assertThrows(IllegalStateException.class, () -> productService.addProduct(testProduct),
                "Should throw an exception for duplicate product");
    }

    // 2) Get All Products Tests
    @Tag("product")
    @Test
    void getProducts_shouldReturnListOfProducts() {
        List<Product> products = productService.getProducts();
        assertNotNull(products, "Products list should not be null");
    }

    @Tag("product")
    @Test
    void getProducts_whenNoProducts_shouldReturnEmptyList() {
        productRepository.clearProducts();
        ArrayList<Product> products = productService.getProducts();
        assertEquals(0, products.size(), "Should return an empty list if no products exist");
    }

    @Tag("product")
    @Test
    void getProducts_whenMultipleProductsExist_shouldReturnCorrectSize() {

        int size = productService.getProducts().size();

        Product secondProduct = new Product();
        secondProduct.setId(UUID.randomUUID());
        secondProduct.setName("Second Product");

        productService.addProduct(testProduct);
        productService.addProduct(secondProduct);

        List<Product> products = productService.getProducts();
        assertEquals(2, products.size() - size, "Product list size should match the number of products added");
    }

    // 3) Get Product By ID Tests
    @Tag("product")
    @Test
    void getProductById_withValidId_shouldReturnProduct() {
        productService.addProduct(testProduct);
        Product retrievedProduct = productService.getProductById(testProduct.getId());
        assertNotNull(retrievedProduct, "Product should be found");
    }

    @Tag("product")
    @Test
    void getProductById_withInvalidId_shouldReturnNull() {
        UUID nonExistentProductId = UUID.randomUUID();
        Product product = productService.getProductById(nonExistentProductId);
        assertNull(product, "If the product is not found, the method should return null.");
    }

    @Tag("product")
    @Test
    void getProductById_withNullId_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> productService.getProductById(null),
                "Should throw an exception if product ID is null");
    }

    // 4) Update Product Tests
    @Tag("product")
    @Test
    void updateProduct_withValidInput_shouldUpdateSuccessfully() throws Exception {
        productService.addProduct(testProduct);
        Product updatedProduct = productService.updateProduct(testProduct.getId(), "Updated Product", 15.0);

        assertEquals("Updated Product", updatedProduct.getName(), "Product name should be updated");
        assertEquals(15.0, updatedProduct.getPrice(), "Product price should be updated");
    }

    @Tag("product")
    @Test
    void updateProduct_whenProductNotFound_shouldThrowException() {
        UUID nonExistentProductId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(nonExistentProductId, "Updated Product", 15.0),
                "Should throw an exception if product is not found");
    }

    @Tag("product")
    @Test
    void updateProduct_withInvalidData_shouldThrowException() {
        productService.addProduct(testProduct);
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(testProduct.getId(), "", -10.0),
                "Should throw an exception for invalid product data");
    }

    // 5) Apply Discount Tests
    @Tag("product")
    @Test
    void applyDiscount_withValidPercentage_shouldApplySuccessfully() {
        productService.addProduct(testProduct);
        ArrayList<UUID> productIds = new ArrayList<>(List.of(testProduct.getId()));

        productService.applyDiscount(20.0, productIds);

        Product updatedProduct = productService.getProductById(testProduct.getId());
        assertEquals(8.0, updatedProduct.getPrice(), "Product price should be discounted correctly");
    }

    @Tag("product")
    @Test
    void applyDiscount_withInvalidPercentage_shouldThrowException() {
        ArrayList<UUID> productIds = new ArrayList<>(List.of(testProduct.getId()));

        assertThrows(IllegalArgumentException.class, () -> productService.applyDiscount(-50.0, productIds),
                "Should throw an exception for negative discount percentage");
    }

    @Tag("product")
    @Test
    void applyDiscount_withEmptyProductList_shouldThrowException() {
        ArrayList<UUID> emptyProductList = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> productService.applyDiscount(20.0, emptyProductList),
                "Should throw an exception if no products are provided for discount");
    }

    // 6) Delete Product Tests
    @Tag("product")
    @Test
    void deleteProduct_withValidId_shouldDeleteSuccessfully() {
        productService.addProduct(testProduct);
        productService.deleteProductById(testProduct.getId());
        Product product = productService.getProductById(testProduct.getId());
        assertNull(product, "Product should be deleted, the method should return null.");
    }

    @Tag("product")
    @Test
    void deleteProduct_whenProductNotFound_shouldThrowException() {
        UUID nonExistentProductId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> productService.deleteProductById(nonExistentProductId),
                "Should throw an exception if product is not found");
    }

    @Tag("product")
    @Test
    void deleteProduct_whenLinkedToOrders_shouldThrowException() {
        productService.addProduct(testProduct);
        orderService.addOrder(testOrder);
        assertThrows(IllegalStateException.class, () -> productService.deleteProductById(testProduct.getId()),
                "Should throw an exception if product is linked to existing orders");
    }

    // ------------------------ Order Tests -------------------------

    // 1) Add Order Tests
    @Tag("order")
    @Test
    void addOrder_withValidInput_shouldReturnSameOrderId() {
        Order createdOrder = orderService.addOrder(testOrder);
        assertEquals(testOrder.getId(), createdOrder.getId(), "Order ID should match");
    }

    @Tag("order")
    @Test
    void addOrder_withInvalidData_shouldThrowException() {
        Order invalidOrder = new Order(); // Missing required fields
        assertThrows(IllegalArgumentException.class, () -> orderService.addOrder(invalidOrder),
                "Should throw an exception for invalid order data");
    }

    @Tag("order")
    @Test
    void addOrder_withDuplicateOrder_shouldThrowException() {
        orderService.addOrder(testOrder);
        assertThrows(IllegalStateException.class, () -> orderService.addOrder(testOrder),
                "Should throw an exception for duplicate order");
    }

    // 2) Get All Orders Tests
    @Tag("order")
    @Test
    void getOrders_shouldReturnListOfOrders() {
        List<Order> orders = orderService.getOrders();
        assertNotNull(orders, "Orders list should not be null");
    }

    @Tag("order")
    @Test
    void getOrders_whenNoOrders_shouldReturnEmptyList() {
        orderRepository.clearOrders();
        List<Order> orders = orderService.getOrders();
        assertEquals(0, orders.size(), "Should return an empty list if no orders exist");
    }

    @Tag("order")
    @Test
    void getOrders_whenMultipleOrdersExist_shouldReturnCorrectSize() {
        int size = orderService.getOrders().size();

        Order secondOrder = new Order();
        secondOrder.setId(UUID.randomUUID());
        secondOrder.setUserId(UUID.randomUUID());

        orderService.addOrder(testOrder);
        orderService.addOrder(secondOrder);

        List<Order> orders = orderService.getOrders();
        assertEquals(2, orders.size() - size, "Orders list size should match the number of carts added.");
    }

    // 3) Get Order By ID Tests
    @Tag("order")
    @Test
    void getOrderById_withValidId_shouldReturnOrder() {
        orderService.addOrder(testOrder);
        Order retrievedOrder = orderService.getOrderById(testOrder.getId());
        assertNotNull(retrievedOrder, "Order should be found");
    }

    @Tag("order")
    @Test
    void getOrderById_withInvalidId_shouldThrowException() {
        UUID nonExistentOrderId = UUID.randomUUID();
        Order order = orderService.getOrderById(nonExistentOrderId);
        assertNull(order,"If order is not found, the method should return null." );
    }

    @Tag("order")
    @Test
    void getOrderById_withNullId_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> orderService.getOrderById(null),
                "Should throw an exception if order ID is null");
    }

    // 4) Delete Order Tests
    @Tag("order")
    @Test
    void deleteOrder_withValidId_shouldDeleteSuccessfully() {
        testOrder.setProducts(new ArrayList<>());
        orderService.addOrder(testOrder);
        orderService.deleteOrderById(testOrder.getId());
        Order order = orderService.getOrderById(testOrder.getId());
        assertNull(order, "Order should be deleted, the method should return null.");
    }

    @Tag("order")
    @Test
    void deleteOrder_whenOrderNotFound_shouldThrowException() {
        UUID nonExistentOrderId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> orderService.deleteOrderById(nonExistentOrderId),
                "Should throw an exception if order is not found");
    }

    @Tag("order")
    @Test
    void deleteOrder_whenOrderHasProducts_shouldThrowException() {
        testOrder.setProducts(List.of(testProduct));
        orderService.addOrder(testOrder);
        assertThrows(IllegalStateException.class, () -> orderService.deleteOrderById(testOrder.getId()),
                "Should throw an exception if order is linked to existing entities");
    }
}