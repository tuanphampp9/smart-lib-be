package com.tuanpham.smart_lib_be.mapper;
import com.tuanpham.smart_lib_be.domain.Post;
import com.tuanpham.smart_lib_be.domain.Request.PostReq;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {
    void updatePost(@MappingTarget Post post, PostReq postReq);
}
