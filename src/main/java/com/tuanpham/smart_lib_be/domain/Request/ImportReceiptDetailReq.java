package com.tuanpham.smart_lib_be.domain.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImportReceiptDetailReq {
    private Double price;
    private int quantity;
    private Long publicationId;
}
