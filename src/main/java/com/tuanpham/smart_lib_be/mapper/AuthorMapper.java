package com.tuanpham.smart_lib_be.mapper;

import com.tuanpham.smart_lib_be.domain.Author;
import com.tuanpham.smart_lib_be.domain.Category;
import com.tuanpham.smart_lib_be.domain.Request.AuthorReq;
import com.tuanpham.smart_lib_be.domain.Request.CategoryReq;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    void updateAuthor(@MappingTarget Author author, AuthorReq authReq);
}
