package com.tuanpham.smart_lib_be.domain.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationReq {
    private Long id;
    private String title;
    private String type;
}
