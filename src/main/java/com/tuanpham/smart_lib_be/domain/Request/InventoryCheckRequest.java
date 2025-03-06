package com.tuanpham.smart_lib_be.domain.Request;

import com.tuanpham.smart_lib_be.domain.Warehouse;
import com.tuanpham.smart_lib_be.util.constant.InventoryCheckStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryCheckRequest {
    private String warehouseId;
    private String note;
    private InventoryCheckStatus status;
}
