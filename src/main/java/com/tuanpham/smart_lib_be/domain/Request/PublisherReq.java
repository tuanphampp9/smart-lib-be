package com.tuanpham.smart_lib_be.domain.Request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublisherReq {
    private String name;
    private String description;
    private String address;
    private String phone;
}
