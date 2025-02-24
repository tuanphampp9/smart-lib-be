package com.tuanpham.smart_lib_be.domain.Request;

import com.tuanpham.smart_lib_be.util.constant.LiquidationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiquidationReq {
    private LiquidationStatus status;
    private String receiverName;
    private String receiverContact;
    private String note;
}
