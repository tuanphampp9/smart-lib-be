package com.tuanpham.smart_lib_be.domain.Response;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class PublisherRes {
    private String id;
    private String name;
    private String description;
    private String address;
    private String phone;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private int numberOfPublications;
}
