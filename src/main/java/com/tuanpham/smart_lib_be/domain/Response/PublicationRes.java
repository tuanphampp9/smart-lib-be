package com.tuanpham.smart_lib_be.domain.Response;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tuanpham.smart_lib_be.domain.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class PublicationRes {
    private Long id;
    private String name;
    private String placeOfPublication;
    private int yearOfPublication;
    private int pageCount;
    private String size;
    private String classify;
    private String isbn;
    private String issn;
    private String bannerImg;
    private String description;
    @JsonIgnoreProperties(value = { "publications", "hibernateLazyInitializer", "handler" })
    private List<Author> authors;
    @JsonIgnoreProperties(value = { "publications", "hibernateLazyInitializer", "handler" })
    private Publisher publisher;
    @JsonIgnoreProperties(value = { "publications", "hibernateLazyInitializer", "handler" })
    private List<Category> categories;

    @JsonIgnoreProperties(value = { "publications", "hibernateLazyInitializer", "handler" })
    private Language language;
    @JsonIgnoreProperties(value = { "publications", "hibernateLazyInitializer", "handler" })
    private Warehouse warehouse;
    @JsonIgnoreProperties(value = { "publications","hibernateLazyInitializer", "handler" })
    private List<Topic> topics;
    private List<PublicationRating> publicationRatings;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    private Long totalQuantity;
}
