package com.tuanpham.smart_lib_be.domain.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryReqCreate {
    private String warehouseId;
    private String userId;
    private String note;
}
