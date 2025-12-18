package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    @Autowired
    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Retrieves a user's shopping cart with all items.
     *
     * @param userId the ID of the user
     * @return the shopping cart containing all items for the user
     * @throws RuntimeException if a database error occurs
     */
    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart shoppingCart = new ShoppingCart();

        String getByIdQuery = """
                SELECT sc.*, p.*
                FROM shopping_cart sc
                JOIN products p ON sc.product_id = p.product_id
                WHERE sc.user_id = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(getByIdQuery)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = mapRow(resultSet);

                    int quantity = resultSet.getInt("quantity");

                    ShoppingCartItem item = new ShoppingCartItem();
                    item.setProduct(product);
                    item.setQuantity(quantity);

                    shoppingCart.add(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return shoppingCart;
    }

    /**
     * Adds a product to a user's shopping cart.
     * If the product already exists, increments the quantity by 1.
     * If the product doesn't exist, adds it with quantity 1.
     *
     * @param userId the ID of the user
     * @param productId the ID of the product to add
     * @throws RuntimeException if a database error occurs
     */
    @Override
    public void addProductToCart(int userId, int productId) {
        String checkQuery = "SELECT quantity FROM shopping_cart WHERE user_id = ? AND product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {

            checkStatement.setInt(1, userId);
            checkStatement.setInt(2, productId);

            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next()) {
                    int currentQuantity = resultSet.getInt("quantity");
                    updateProductQuantity(userId, productId, currentQuantity + 1);
                } else {
                    String insertQuery = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, 1)";

                    try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

                        insertStatement.setInt(1, userId);
                        insertStatement.setInt(2, productId);
                        insertStatement.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the quantity of a product in a user's shopping cart.
     *
     * @param userId the ID of the user
     * @param productId the ID of the product to update
     * @param quantity the new quantity
     * @throws RuntimeException if the product is not found in the cart or a database error occurs
     */
    @Override
    public void updateProductQuantity(int userId, int productId, int quantity) {
        String updateQuery = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {

            statement.setInt(1, quantity);
            statement.setInt(2, userId);
            statement.setInt(3, productId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Updating product failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Clears all items from a user's shopping cart.
     *
     * @param userId the ID of the user
     * @throws RuntimeException if a database error occurs
     */
    @Override
    public void clearCart(int userId) {
        String deleteQuery = "DELETE FROM shopping_cart WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteQuery)) {

            statement.setInt(1, userId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected static Product mapRow(ResultSet row) throws SQLException {
        int productId = row.getInt("product_id");
        String name = row.getString("name");
        BigDecimal price = row.getBigDecimal("price");
        int categoryId = row.getInt("category_id");
        String description = row.getString("description");
        String subCategory = row.getString("subcategory");
        int stock = row.getInt("stock");
        boolean isFeatured = row.getBoolean("featured");
        String imageUrl = row.getString("image_url");

        return new Product(productId, name, price, categoryId, description, subCategory, stock, isFeatured, imageUrl);
    }
}
