package com.tuanpham.smart_lib_be.domain.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqChangePassword {
    private String email;
    private String oldPassword;
    private String newPassword;
}
