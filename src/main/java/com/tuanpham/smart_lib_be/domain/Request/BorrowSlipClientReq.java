package com.tuanpham.smart_lib_be.domain.Request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BorrowSlipClientReq {
    private String cardId;
    private List<String> cartIds;
}
