package com.tuanpham.smart_lib_be.mapper;

import com.tuanpham.smart_lib_be.domain.Language;
import com.tuanpham.smart_lib_be.domain.Publication;
import com.tuanpham.smart_lib_be.domain.Request.LanguageReq;
import com.tuanpham.smart_lib_be.domain.Request.PublicationReq;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PublicationMapper {
    void updatePublication(@MappingTarget Publication publication, PublicationReq publicationReq);
}
