package com.tuanpham.smart_lib_be.mapper;

import com.tuanpham.smart_lib_be.domain.Publication;
import com.tuanpham.smart_lib_be.domain.PublicationRequest;
import com.tuanpham.smart_lib_be.domain.Request.PubReqRes;
import com.tuanpham.smart_lib_be.domain.Request.PublicationReq;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PublicationRequestMapper {
    void updatePublication(@MappingTarget PublicationRequest publicationRequest, PubReqRes pubReqRes);
}
