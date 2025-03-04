package com.tuanpham.smart_lib_be.domain.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiquidationCrqReq {
    private String receiverName;
    private String receiverContact;
    private String note;
    private String userId;
}
