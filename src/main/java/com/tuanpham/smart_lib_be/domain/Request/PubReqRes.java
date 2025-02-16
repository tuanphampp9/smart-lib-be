package com.tuanpham.smart_lib_be.domain.Request;

import com.tuanpham.smart_lib_be.util.constant.PublicationRequestStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PubReqRes {
    private PublicationRequestStatus status;
    private String response;
}
