package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.Post;
import com.tuanpham.smart_lib_be.domain.Request.PostReq;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.mapper.PostMapper;
import com.tuanpham.smart_lib_be.repository.PostRepository;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    
    public PostService(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }
    
    public Post handleCreatePost(Post post) {
        return this.postRepository.save(post);
    }
    
    public Post handleUpdatePost(PostReq postReq, Long id) throws IdInvalidException {
        Post postToUpdate = this.postRepository.findById(id).orElse(null);
        if (postToUpdate == null) {
            throw new IdInvalidException("Bài viết không tồn tại");
        }
        this.postMapper.updatePost(postToUpdate, postReq);
        return this.postRepository.save(postToUpdate);
    }
    
    public Post handleGetPostById(Long id) {
        return this.postRepository.findById(id).orElse(null);
    }

    public Post handleUpdateViewCount(Post post) {
        post.setViewCount(post.getViewCount() + 1);
        return this.postRepository.save(post);
    }

    public ResultPaginationDTO handleGetAllPosts(Specification<Post> spec,
                                                      Pageable pageable) {
        Page<Post> pagePosts = this.postRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pagePosts.getSize());
        meta.setTotal(pagePosts.getTotalElements());// amount of elements
        meta.setPages(pagePosts.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<Post> listPosts = pagePosts.getContent().stream().map(
                        p-> p)
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listPosts);
        return resultPaginationDTO;
    }
    public void handleDeletePost(Long id) {
        this.postRepository.deleteById(id);
    }
}
