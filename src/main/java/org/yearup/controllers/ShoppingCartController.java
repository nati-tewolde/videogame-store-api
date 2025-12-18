package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("/cart")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController {
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    /**
     * Retrieves the current user's shopping cart.
     *
     * @param principal the authenticated user
     * @return the user's shopping cart with all items
     * @throws ResponseStatusException if an error occurs
     */
    @GetMapping
    public ShoppingCart getCart(Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            return shoppingCartDao.getByUserId(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    /**
     * Adds a product to the current user's shopping cart.
     * If the product already exists in the cart, its quantity is incremented by 1.
     *
     * @param productId the ID of the product to add
     * @param principal the authenticated user
     * @return the updated shopping cart
     * @throws ResponseStatusException if the product is not found or an error occurs
     */
    @PostMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCart addProductToCart(@PathVariable int productId, Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            Product product = productDao.getById(productId);
            if (product == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
            }

            shoppingCartDao.addProductToCart(userId, productId);

            return shoppingCartDao.getByUserId(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    /**
     * Updates the quantity of a product in the current user's shopping cart.
     *
     * @param productId the ID of the product to update
     * @param item a map containing the new quantity
     * @param principal the authenticated user
     * @return the updated shopping cart
     * @throws ResponseStatusException if the quantity is invalid or an error occurs
     */
    @PutMapping("/products/{productId}")
    public ShoppingCart updateProductInCart(@PathVariable int productId,
                                            @RequestBody ShoppingCartItem item,
                                            Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            int quantity = item.getQuantity();
            if (quantity < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid quantity");
            }

            shoppingCartDao.updateProductQuantity(userId, productId, quantity);

            return shoppingCartDao.getByUserId(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    /**
     * Clears all items from the current user's shopping cart.
     *
     * @param principal the authenticated user
     * @throws ResponseStatusException if an error occurs
     */
    @DeleteMapping
    public ShoppingCart clearCart(Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            shoppingCartDao.clearCart(userId);

            return new ShoppingCart();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
