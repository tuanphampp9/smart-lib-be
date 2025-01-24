package com.tuanpham.smart_lib_be.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tuanpham.smart_lib_be.util.SecurityUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "publications")
@Getter
@Setter
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(columnDefinition = "TEXT")
    private String description;

    // many publications belong to many authors (owner)
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "publications" })
    @JoinTable(name = "author_publication",
            joinColumns = @JoinColumn(name = "publication_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id"))
    private List<Author> authors;

    // many publications belong to one publisher (owner)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "publisher_id")
    @JsonIgnoreProperties(value = { "publications" })
    private Publisher publisher;

    // many publications belong to many categories (owner)
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "publications" })
    @JoinTable(name = "category_publication",
            joinColumns = @JoinColumn(name = "publication_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories;

    // one publication have one language
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language_id")
    @JsonIgnoreProperties(value = { "publications" })
    private Language language;

    // many publications belong to one warehouse
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "warehouse_id")
    @JsonIgnoreProperties(value = { "publications" })
    private Warehouse warehouse;

    // many publications belong to many topics
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "publications" })
    @JoinTable(name = "topic_publication",
            joinColumns = @JoinColumn(name = "publication_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private List<Topic> topics;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @PrePersist // action before save
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.createdAt = Instant.now();
    }

    @PreUpdate // action before update
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.updatedAt = Instant.now();
    }
}
