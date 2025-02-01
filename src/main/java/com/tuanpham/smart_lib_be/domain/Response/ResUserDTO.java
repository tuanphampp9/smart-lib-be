package com.tuanpham.smart_lib_be.domain.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tuanpham.smart_lib_be.domain.CardRead;
import com.tuanpham.smart_lib_be.domain.CartUser;
import com.tuanpham.smart_lib_be.util.constant.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResUserDTO {
    private String id;
    private String email;
    private String fullName;
    private GenderEnum gender;
    private String address;
    private String portraitImg;
    private String dob;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;
    private role role;
    private String identityCardNumber;
    private String phone;
    private boolean active;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "serves"})// ignore serves
    private CardRead cardRead;
    private List<CartUserRes> cartUsers;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class role {
        private long id;
        private String name;
    }
}
