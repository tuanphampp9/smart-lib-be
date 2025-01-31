package com.tuanpham.smart_lib_be.domain.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class AuthorRes {
    private String id;
    private String fullName;
    private String penName;
    private String homeTown;
    private String introduction;
    private String avatar;
    private String dob;
    private String dod;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private int numberOfPublications;
}
