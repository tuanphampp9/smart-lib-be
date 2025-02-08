package com.tuanpham.smart_lib_be.mapper;

import com.tuanpham.smart_lib_be.domain.ImportReceiptDetail;
import com.tuanpham.smart_lib_be.domain.Response.ImportReceiptDetailRes;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImportReceiptMapper {
    ImportReceiptDetailRes toImportReceiptDetailRes(ImportReceiptDetail importReceiptDetail);
}
