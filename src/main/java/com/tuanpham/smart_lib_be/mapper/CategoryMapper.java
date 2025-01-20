package com.tuanpham.smart_lib_be.mapper;

import com.tuanpham.smart_lib_be.domain.Category;
import com.tuanpham.smart_lib_be.domain.Request.CategoryReq;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    void updateCategory(@MappingTarget Category category, CategoryReq categoryReq);
}
