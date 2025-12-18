package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/categories")
public class CategoriesController {
    private CategoryDao categoryDao;
    private ProductDao productDao;

    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    /**
     * Retrieves all categories from the database.
     *
     * @return a list of all categories
     */
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<Category> getAll() {
        return categoryDao.getAllCategories();
    }

    /**
     * Retrieves a specific category by its ID.
     *
     * @param id the category ID to retrieve
     * @return the category with the specified ID
     * @throws ResponseStatusException if the category is not found
     */
    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    public Category getById(@PathVariable int id) {
        Category category = categoryDao.getById(id);

        if (category == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }

        return category;
    }

    /**
     * Retrieves all products that belong to a specific category.
     *
     * @param categoryId the ID of the category
     * @return a list of products in the specified category
     * @throws ResponseStatusException if the category is not found
     */
    @GetMapping("{categoryId}/products")
    @PreAuthorize("permitAll()")
    public List<Product> getProductsById(@PathVariable int categoryId) {
        try {
            Category category = categoryDao.getById(categoryId);

            if (category == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
            }

            return productDao.listByCategoryId(categoryId);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    /**
     * Creates a new category in the database.
     * Only administrators can perform this action.
     *
     * @param category the category object to create
     * @return the newly created category with its generated ID
     * @throws ResponseStatusException if an error occurs during creation
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Category addCategory(@RequestBody Category category) {
        try {
            return categoryDao.create(category);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    /**
     * Updates an existing category in the database.
     * Only administrators can perform this action.
     *
     * @param id the ID of the category to update
     * @param category the updated category information
     * @throws ResponseStatusException if the category is not found or an error occurs
     */
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateCategory(@PathVariable int id, @RequestBody Category category) {
        try {
            Category existingCategory = categoryDao.getById(id);
            if (existingCategory == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
            }

            categoryDao.update(id, category);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    /**
     * Deletes a category from the database.
     * Only administrators can perform this action.
     *
     * @param id the ID of the category to delete
     * @throws ResponseStatusException if the category is not found or an error occurs
     */
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCategory(@PathVariable int id) {
        try {
            Category category = categoryDao.getById(id);
            if (category == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
            }

            categoryDao.delete(id);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
