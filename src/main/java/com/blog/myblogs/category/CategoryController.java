package com.blog.myblogs.category;

import com.blog.myblogs.common.ApiResponse;
import com.blog.myblogs.common.ResponseGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseGenerator.generateResponse("Categories fetched successfully", HttpStatus.OK, categories);
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getCategoryCount() {
        long count = categoryService.getCount();
        return ResponseGenerator.generateResponse("Category count fetched successfully", HttpStatus.OK, count);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Category>> saveCategory(@RequestBody CategoryDTO dto) {
        Category saved = categoryService.saveCategory(dto);
        return ResponseGenerator.generateResponse("Category saved successfully", HttpStatus.CREATED, saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        Category updated = categoryService.updateCategory(id, dto);
        return ResponseGenerator.generateResponse("Category updated successfully", HttpStatus.OK, updated);
    }

    @GetMapping("/{id}/delete-preview")
    public ResponseEntity<ApiResponse<String>> deleteCategoryPreview(@PathVariable Long id) {
        int postCount = 0;
        try {
            Category category = categoryService.getCategoryEntityById(id);
            postCount = category.getPosts() != null ? category.getPosts().size() : 0;
        } catch (Exception ignored) {
        }
        String message = "Deleting this category will also delete " + postCount
                + " posts associated with this category. Are you sure you want to proceed?";
        return ResponseGenerator.generateResponse(message, HttpStatus.OK, null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id,
            @RequestParam(value = "confirm", required = false) Boolean confirm) {
        int postCount = 0;
        try {
            Category category = categoryService.getCategoryEntityById(id);
            postCount = category.getPosts() != null ? category.getPosts().size() : 0;
        } catch (Exception ignored) {
        }
        if (postCount > 0 && (confirm == null || !confirm)) {
            String message = "Confirmation required: Deleting this category will also delete " + postCount
                    + " posts.";
            return ResponseGenerator.generateResponse(message, HttpStatus.BAD_REQUEST, null);
        }
        categoryService.deleteCategory(id);
        String message = "Category deleted successfully.";
        return ResponseGenerator.generateResponse(message, HttpStatus.OK, null);
    }
}
