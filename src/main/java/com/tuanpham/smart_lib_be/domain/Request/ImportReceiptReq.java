package com.tuanpham.smart_lib_be.domain.Request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImportReceiptReq {
    List<ImportReceiptDetailReq> importReceiptDetailReqs;
    private String inputSource;
    private String deliveryPerson;
    private String deliveryRepresentative;
    private String note;
}
