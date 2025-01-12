package com.tuanpham.smart_lib_be.domain.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tuanpham.smart_lib_be.util.constant.GenderEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateDTO {
    private String id;
    private String fullName;
    private String address;
    private GenderEnum gender;
    private String portraitImg;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;

    @Getter
    @Setter
    public static class company {
        private long id;
        private String name;
    }
}
