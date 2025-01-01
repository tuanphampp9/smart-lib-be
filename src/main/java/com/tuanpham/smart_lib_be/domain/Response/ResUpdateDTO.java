package com.tuanpham.smart_lib_be.domain.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tuanpham.smart_lib_be.util.constant.GenderEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateDTO {
    private long id;
    private String name;
    private int age;
    private String address;
    private GenderEnum gender;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;
    private company company;

    @Getter
    @Setter
    public static class company {
        private long id;
        private String name;
    }
}
