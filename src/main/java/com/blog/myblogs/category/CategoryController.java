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

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseGenerator.generateResponse("Category deleted successfully", HttpStatus.OK, null);
    }
}
