package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.*;
import org.yearup.models.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class OrdersController {

    private OrderDao orderDao;
    private OrderLineItemDao orderLineItemDao;
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProfileDao profileDao;

    @Autowired
    public OrdersController(OrderDao orderDao, OrderLineItemDao orderLineItemDao, ShoppingCartDao shoppingCartDao, UserDao userDao, ProfileDao profileDao) {
        this.orderDao = orderDao;
        this.orderLineItemDao = orderLineItemDao;
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.profileDao = profileDao;
    }

    /**
     * Processes checkout by converting the user's shopping cart into an order.
     * Creates an order record and order line items for each cart item.
     * Clears the shopping cart after successful order creation.
     *
     * @param principal the authenticated user
     * @return the created order with generated order ID
     * @throws ResponseStatusException if the cart is empty, profile is not found, or an error occurs
     */
    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Order checkout(Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            ShoppingCart cart = shoppingCartDao.getByUserId(userId);
            if (cart.getItems().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot checkout with empty cart");
            }

            Profile profile = profileDao.getByUserId(userId);
            if (profile == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User profile not found");
            }

            Order order = new Order();
            order.setUserId(userId);
            order.setDate(LocalDateTime.now());
            order.setAddress(profile.getAddress());
            order.setCity(profile.getCity());
            order.setState(profile.getState());
            order.setZip(profile.getZip());
            order.setShippingAmount(BigDecimal.ZERO);

            order = orderDao.create(order);

            for (ShoppingCartItem cartItem : cart.getItems().values()) {
                OrderLineItem lineItem = new OrderLineItem();

                lineItem.setOrderId(order.getOrderId());
                lineItem.setProductId(cartItem.getProduct().getProductId());
                lineItem.setSalesPrice(cartItem.getProduct().getPrice());
                lineItem.setQuantity(cartItem.getQuantity());
                lineItem.setDiscount(BigDecimal.ZERO);

                orderLineItemDao.create(lineItem);
            }

            shoppingCartDao.clearCart(userId);

            return order;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
