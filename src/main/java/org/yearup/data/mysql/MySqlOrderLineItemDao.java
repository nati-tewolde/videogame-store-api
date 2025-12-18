package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.OrderLineItemDao;
import org.yearup.models.OrderLineItem;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlOrderLineItemDao extends MySqlDaoBase implements OrderLineItemDao {

    @Autowired
    public MySqlOrderLineItemDao(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Creates a new order line item in the database.
     *
     * @param orderLineItem the order line item to create
     * @throws RuntimeException if a database error occurs
     */
    @Override
    public void create(OrderLineItem orderLineItem) {
        String insertQuery = "INSERT INTO order_line_items (order_id, product_id, sales_price, quantity, discount) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, orderLineItem.getOrderId());
            statement.setInt(2, orderLineItem.getProductId());
            statement.setBigDecimal(3, orderLineItem.getSalesPrice());
            statement.setInt(4, orderLineItem.getQuantity());
            statement.setBigDecimal(5, orderLineItem.getDiscount());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Creating order line item failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    orderLineItem.setOrderLineItemId(generatedId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
