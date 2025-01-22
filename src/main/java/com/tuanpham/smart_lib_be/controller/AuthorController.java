package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.Author;
import com.tuanpham.smart_lib_be.domain.Category;
import com.tuanpham.smart_lib_be.domain.Request.AuthorReq;
import com.tuanpham.smart_lib_be.domain.Request.CategoryReq;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.service.AuthorService;
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
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping("/authors")
    public ResponseEntity<Author> create(@Valid @RequestBody Author author)
            throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.authorService.handleCreateAuthor(author));
    }

    @PutMapping("/authors/{id}")
    public ResponseEntity<Author> update(@Valid @RequestBody AuthorReq authorReq, @PathVariable("id") String id)
            throws IdInvalidException {
        return ResponseEntity.ok().body(this.authorService.handleUpdateAuthor(authorReq, id));
    }

    @GetMapping("/authors/{id}")
    public ResponseEntity<Author> getCategoryById(@PathVariable("id") String id)
            throws IdInvalidException {
        Author author = this.authorService.handleFindAuthorById(id);
        if (author == null) {
            throw new IdInvalidException("Tác giả không tồn tại");
        }
        return ResponseEntity.ok().body(author);
    }

    @GetMapping("/authors")
    public ResponseEntity<ResultPaginationDTO> getAllCategories(
            @Filter Specification<Author> spec, Pageable pageable
    ) {
        return ResponseEntity.ok().body(this.authorService.handleGetAllCategories(spec, pageable));
    }

    @DeleteMapping("/authors/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable("id") String id)
            throws IdInvalidException {
        Author author = this.authorService.handleFindAuthorById(id);
        if (author == null) {
            throw new IdInvalidException("Tác giả không tồn tại");
        }
        this.authorService.handleDeleteAuthor(id);
        return ResponseEntity.ok().body("Xóa tác giả thành công");
    }
}
