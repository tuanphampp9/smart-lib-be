package com.tuanpham.smart_lib_be.controller;


import com.tuanpham.smart_lib_be.domain.CardRead;
import com.tuanpham.smart_lib_be.domain.Category;
import com.tuanpham.smart_lib_be.domain.Request.CategoryReq;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.domain.Role;
import com.tuanpham.smart_lib_be.service.CategoryService;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/categories")
    public ResponseEntity<Category> create(@Valid @RequestBody Category category)
            throws IdInvalidException {
        boolean isExist = this.categoryService.handleCategoryExist(category.getName());
        if (isExist) {
            throw new IdInvalidException("Thể loại đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.categoryService.handleCreateCategory(category));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<Category> update(@Valid @RequestBody CategoryReq categoryReq, @PathVariable("id") String id)
            throws IdInvalidException {
        return ResponseEntity.ok().body(this.categoryService.handleUpdateCategory(categoryReq, id));
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable("id") String id)
            throws IdInvalidException {
        Category category = this.categoryService.handleFindCategoryById(id);
        if (category == null) {
            throw new IdInvalidException("Thể loại không tồn tại");
        }
        return ResponseEntity.ok().body(category);
    }

    @GetMapping("/categories")
    public ResponseEntity<ResultPaginationDTO> getAllCategories(
            @Filter Specification<Category> spec, Pageable pageable
    ) {
        return ResponseEntity.ok().body(this.categoryService.handleGetAllCategories(spec, pageable));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable("id") String id)
            throws IdInvalidException {
        Category category = this.categoryService.handleFindCategoryById(id);
        if (category == null) {
            throw new IdInvalidException("Thể loại không tồn tại");
        }
        this.categoryService.handleDeleteCategory(id);
        return ResponseEntity.ok().body("Xóa thể loại thành công");
    }
}
