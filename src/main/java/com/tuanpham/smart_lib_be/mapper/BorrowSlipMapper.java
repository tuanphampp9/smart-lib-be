package com.tuanpham.smart_lib_be.mapper;

import com.tuanpham.smart_lib_be.domain.BorrowSlip;
import com.tuanpham.smart_lib_be.domain.Response.BorrowSlipRes;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BorrowSlipMapper {
    BorrowSlipRes toBorrowSlipRes(BorrowSlip borrowSlip);
}
