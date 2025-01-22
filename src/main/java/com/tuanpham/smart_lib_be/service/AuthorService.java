package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.Author;
import com.tuanpham.smart_lib_be.domain.Category;
import com.tuanpham.smart_lib_be.domain.Request.AuthorReq;
import com.tuanpham.smart_lib_be.domain.Request.CategoryReq;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.mapper.AuthorMapper;
import com.tuanpham.smart_lib_be.mapper.CategoryMapper;
import com.tuanpham.smart_lib_be.repository.AuthorRepository;
import com.tuanpham.smart_lib_be.repository.CategoryRepository;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    public Author handleCreateAuthor(Author author) {
        return this.authorRepository.save(author);
    }
    public Author handleFindAuthorById(String id) {
        return this.authorRepository.findById(id).orElse(null);
    }

    public Author handleUpdateAuthor(AuthorReq authorReq, String id) throws IdInvalidException {
        Author authorExist = this.authorRepository.findById(id).orElse(null);
        if (authorExist == null) {
            throw new IdInvalidException("Tác giả không tồn tại");
        }
        this.authorMapper.updateAuthor(authorExist, authorReq);
        return this.authorRepository.save(authorExist);
    }

    public ResultPaginationDTO handleGetAllCategories(Specification<Author> spec,
                                                      Pageable pageable) {
        Page<Author> pageAuthors = this.authorRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageAuthors.getSize());
        meta.setTotal(pageAuthors.getTotalElements());// amount of elements
        meta.setPages(pageAuthors.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<Author> listAuthors = pageAuthors.getContent().stream().map(
                        c -> c)
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listAuthors);
        return resultPaginationDTO;
    }

    public void handleDeleteAuthor(String id) {
        this.authorRepository.deleteById(id);
    }
}
