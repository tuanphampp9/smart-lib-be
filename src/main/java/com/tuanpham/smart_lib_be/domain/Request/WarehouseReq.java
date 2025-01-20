package com.tuanpham.smart_lib_be.domain.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseReq {
    private String type;
    private String name;
    private String description;
}
