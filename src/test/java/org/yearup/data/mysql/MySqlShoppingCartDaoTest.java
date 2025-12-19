package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MySqlShoppingCartDaoTest extends BaseDaoTestClass {
    private MySqlShoppingCartDao dao;

    @BeforeEach
    public void setup() {
        dao = new MySqlShoppingCartDao(dataSource);
    }

    @Test
    public void getByUserId_shouldReturn_emptyCart_whenUserHasNoItems() {
        // Arrange
        int userId = 1;

        // Act
        ShoppingCart cart = dao.getByUserId(userId);

        // Assert
        assertTrue(cart.getItems().isEmpty(), "Cart should be empty");
        assertEquals(new BigDecimal("0"), cart.getTotal(), "Total should be 0 for empty cart");
    }

    @Test
    public void addProductToCart_shouldAdd_newProduct() {
        // Arrange
        int userId = 1;
        int productId = 1;

        // Act
        dao.addProductToCart(userId, productId);
        ShoppingCart cart = dao.getByUserId(userId);
        ShoppingCartItem item = cart.getItems().get(productId);

        // Assert
        assertFalse(cart.getItems().isEmpty(), "Cart should have items after adding");
        assertTrue(cart.getItems().containsKey(productId), "Cart should contain the added product");
        assertEquals(1, item.getQuantity(), "New product should have quantity of 1");
        assertEquals("Smartphone", item.getProduct().getName(), "Product name should match");
    }

    @Test
    public void updateProductQuantity_shouldUpdate_existingItem() {
        // Arrange
        int userId = 1;
        int productId = 1;
        int newQuantity = 10;
        dao.addProductToCart(userId, productId);

        // Act
        dao.updateProductQuantity(userId, productId, newQuantity);
        ShoppingCart cart = dao.getByUserId(userId);
        ShoppingCartItem item = cart.getItems().get(productId);

        // Assert
        assertEquals(newQuantity, item.getQuantity(), "Quantity should be updated to 10");
    }

    @Test
    public void clearCart_shouldRemove_allItems() {
        // Arrange
        int userId = 1;
        dao.addProductToCart(userId, 1);
        dao.addProductToCart(userId, 2);
        dao.addProductToCart(userId, 3);

        // Act
        dao.clearCart(userId);
        ShoppingCart cart = dao.getByUserId(userId);

        // Assert
        assertTrue(cart.getItems().isEmpty(), "Cart should be empty after clearing");
    }
}