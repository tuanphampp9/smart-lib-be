package com.tuanpham.smart_lib_be.mapper;

import com.tuanpham.smart_lib_be.domain.Publisher;
import com.tuanpham.smart_lib_be.domain.Request.PublisherReq;
import com.tuanpham.smart_lib_be.domain.Request.WarehouseReq;
import com.tuanpham.smart_lib_be.domain.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    void updateWarehouse(@MappingTarget Warehouse warehouse, WarehouseReq warehouseReq);
}
