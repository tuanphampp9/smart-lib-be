package com.tuanpham.smart_lib_be.mapper;

import com.tuanpham.smart_lib_be.domain.Publisher;
import com.tuanpham.smart_lib_be.domain.Request.PublisherReq;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PublisherMapper {
    void updatePublisher(@MappingTarget Publisher publisher, PublisherReq publisherReq);
}
