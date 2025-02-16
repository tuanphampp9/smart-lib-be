package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.Post;
import com.tuanpham.smart_lib_be.domain.Request.PostReq;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.service.PostService;
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
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/posts")
    public ResponseEntity<Post> create(@Valid @RequestBody Post post)
             {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.postService.handleCreatePost(post));
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<Post> update(@Valid @RequestBody PostReq postReq, @PathVariable("id") Long id)
            throws IdInvalidException {
        return ResponseEntity.ok().body(this.postService.handleUpdatePost(postReq, id));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable("id") Long id)
            throws IdInvalidException {
        Post post = this.postService.handleGetPostById(id);
        if (post == null) {
            throw new IdInvalidException("Bài viết không tồn tại");
        }
        return ResponseEntity.ok().body(post);
    }
    @GetMapping("/posts/client/{id}")
    public ResponseEntity<Post> getPostForClientById(@PathVariable("id") Long id)
            throws IdInvalidException {
        Post post = this.postService.handleGetPostById(id);
        if (post == null) {
            throw new IdInvalidException("Bài viết không tồn tại");
        }
        return ResponseEntity.ok().body(this.postService.handleUpdateViewCount(post));
    }

    @GetMapping("/posts")
    public ResponseEntity<ResultPaginationDTO> getAllPosts(
            @Filter Specification<Post> spec, Pageable pageable
    ) {
        return ResponseEntity.ok().body(this.postService.handleGetAllPosts(spec, pageable));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<String> deletePost(@PathVariable("id") Long id)
            throws IdInvalidException {
        Post post = this.postService.handleGetPostById(id);
        if (post == null) {
            throw new IdInvalidException("Bài viết không tồn tại");
        }
        this.postService.handleDeletePost(id);
        return ResponseEntity.ok().body("Xóa bài viết thành công");
    }
}
