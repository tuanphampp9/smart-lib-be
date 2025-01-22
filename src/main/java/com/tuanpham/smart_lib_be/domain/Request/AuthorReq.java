package com.tuanpham.smart_lib_be.domain.Request;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class AuthorReq {
    private String fullName;
    private String penName;
    private String homeTown;
    private String introduction;
    private String dob;
    private String dod;
    private String avatar;
}
