package com.tuanpham.smart_lib_be.domain.Request;

import com.tuanpham.smart_lib_be.util.constant.LiquidationDetailStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiquidationDetailCreate {
    private String registrationId;
    private LiquidationDetailStatus conditionStatus;
    private String note;
    private Double price;
}
