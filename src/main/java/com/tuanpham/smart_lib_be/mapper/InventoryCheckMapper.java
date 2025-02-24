package com.tuanpham.smart_lib_be.mapper;

import com.tuanpham.smart_lib_be.domain.Category;
import com.tuanpham.smart_lib_be.domain.InventoryCheck;
import com.tuanpham.smart_lib_be.domain.Request.CategoryReq;
import com.tuanpham.smart_lib_be.domain.Request.InventoryCheckRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InventoryCheckMapper {
    void updateInventoryCheck(@MappingTarget InventoryCheck inventoryCheck, InventoryCheckRequest inventoryCheckRequest);
}
