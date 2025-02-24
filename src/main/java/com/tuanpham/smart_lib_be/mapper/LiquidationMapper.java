package com.tuanpham.smart_lib_be.mapper;

import com.tuanpham.smart_lib_be.domain.InventoryCheck;
import com.tuanpham.smart_lib_be.domain.Liquidation;
import com.tuanpham.smart_lib_be.domain.Request.InventoryCheckRequest;
import com.tuanpham.smart_lib_be.domain.Request.LiquidationReq;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LiquidationMapper {
    void updateLiquidation(@MappingTarget Liquidation liquidation, LiquidationReq liquidationReq);
}
