# Video Game Store (Rest API)

## Project Description 

Video Game Store is a Spring Boot REST API for an online video game store backend. 
Using the associated web application, users can browse games and products by category, search and filter items, 
login to update their profiles, manage a shopping cart, and checkout with their orders.
Administrators can additionally create, update, and delete categories/products. 

## User Stories
<details>
  <summary>Click to expand user stories</summary>

- As an online customer, I want to browse products by category, so that I can easily find the products I'm looking for.
- As an online customer, I want search results to accurately match my filters, so that I can find the exact products I need.
- As an online customer, I want to add items to my shopping cart, so that I can purchase multiple products at once.
- As an online customer, I want to view and update my profile information, so that my account details stay current.
- As an online customer, I want to complete my purchase, so that I can receive my selected products.
</details>

## Setup

Instructions on how to set up and run the application:

### Prerequisites

- IntelliJ IDEA: Ensure you have IntelliJ IDEA installed, which you can download from [here](https://www.jetbrains.com/idea/download/).
- Java SDK: Make sure Java SDK is installed and configured in IntelliJ.
- MySQL: Install MySQL Server and MySQL Workbench from [here](https://dev.mysql.com/downloads/).

### Database Setup

1. Open MySQL Workbench and connect to your MySQL server.
2. Navigate to the `database` folder in the project directory.
3. Open the `create_database_videogamestore.sql` script in MySQL Workbench.
4. Execute the script to create the `videogamestore` database with sample products and users.

### Running the Application in IntelliJ

Follow these steps to get your application running within IntelliJ IDEA:

1. Open IntelliJ IDEA.
2. Select "Open" and navigate to the directory where you cloned or downloaded the project.
3. After the project opens, wait for IntelliJ to index the files and set up the project.
4. Configure the database connection in `application.properties`.
5. Find the main Spring Boot application class with the `@SpringBootApplication` annotation.
6. Right-click on the file and select 'Run `EasyshopApplication.main()`' to start the application.

## Technologies Used

- Java: Amazon Corretto 17.0.16
- Spring Boot
- Insomnia
- MySQL Workbench 8.0 CE
- JUnit 5 (Jupiter)

## Application Demo

### Filtering by category
![Category Demo](./demos/category-demo.gif)

### Filtering by product's price
![Category Demo](./demos/product-demo.gif)

### User login & updating profile
![Category Demo](./demos/login-profile-demo.gif)

### Admin login & managing cart
![Category Demo](./demos/login-cart-demo.gif)

## Future Work

- **Frontend Features**: implement frontend for user checkout and administrator category/product management.
- **Order History**: view past orders with detailed receipts and tracking information.
- **Wishlist Items**: enable users to save items to a wishlist for future purchase.

## Resources

- [Workbook 9 Resources](https://github.com/RayMaroun/yearup-fall-section-8-2025/tree/main/pluralsight/java-development/workbook-9)
- [HTTP Response Status Codes - Mozilla]( https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status)
- [Java SQL Prepared Statement (Timestamp) - Oracle](https://docs.oracle.com/javase/8/docs/api/java/sql/PreparedStatement.html)
- [Assertions - JUnit](https://junit.org/junit4/javadoc/4.8/org/junit/Assert.html)

## Credits

- Thank you to both **Raymond** and **Andy** for supporting me throughout my debugging journey!
