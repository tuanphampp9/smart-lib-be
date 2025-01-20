package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.CardRead;
import com.tuanpham.smart_lib_be.domain.Category;
import com.tuanpham.smart_lib_be.domain.Request.CategoryReq;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.mapper.CategoryMapper;
import com.tuanpham.smart_lib_be.mapper.TopicMapper;
import com.tuanpham.smart_lib_be.repository.CategoryRepository;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public boolean handleCategoryExist(String name) {
        return this.categoryRepository.existsByName(name);
    }

    public Category handleCreateCategory(Category category) {
        return this.categoryRepository.save(category);
    }
    public Category handleFindCategoryById(String id) {
        return this.categoryRepository.findById(id).orElse(null);
    }

    public Category handleUpdateCategory(CategoryReq categoryReq, String id) throws IdInvalidException {
        Category categoryExist = this.categoryRepository.findById(id).orElse(null);
        if (categoryExist == null) {
            throw new IdInvalidException("Thể loại không tồn tại");
        }
        if(this.categoryRepository.existsByNameAndIdNot(categoryReq.getName(), id)) {
            throw new IdInvalidException("Thể loại đã tồn tại");
        }
        this.categoryMapper.updateCategory(categoryExist, categoryReq);
        return this.categoryRepository.save(categoryExist);
    }

    public ResultPaginationDTO handleGetAllCategories(Specification<Category> spec,
                                                       Pageable pageable) {
        Page<Category> pageCategories = this.categoryRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageCategories.getSize());
        meta.setTotal(pageCategories.getTotalElements());// amount of elements
        meta.setPages(pageCategories.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<Category> listCategories = pageCategories.getContent().stream().map(
                        c -> c)
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listCategories);
        return resultPaginationDTO;
    }

    public void handleDeleteCategory(String id) {
        this.categoryRepository.deleteById(id);
    }
}
