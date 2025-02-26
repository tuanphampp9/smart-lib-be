package com.tuanpham.smart_lib_be.mapper;

import com.tuanpham.smart_lib_be.domain.Language;
import com.tuanpham.smart_lib_be.domain.Request.LanguageReq;
import com.tuanpham.smart_lib_be.domain.Response.LanguageRes;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LanguageMapper {
    void updateLanguage(@MappingTarget Language language, LanguageReq languageReq);
    LanguageRes toLanguageRes(Language language);
}
