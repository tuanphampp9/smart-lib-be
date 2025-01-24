package com.tuanpham.smart_lib_be.domain.Request;

import com.tuanpham.smart_lib_be.domain.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PublicationReq {
    private Long id;
    private String name;
    private String placeOfPublication;
    private int yearOfPublication;
    private int pageCount;
    private String size;
    private String classify;
    private String isbn;
    private String issn;
    private String description;
    private List<Author> authors;
    private Publisher publisher;
    private List<Category> categories;
    private Language language;
    private Warehouse warehouse;
    private List<Topic> topics;
}
