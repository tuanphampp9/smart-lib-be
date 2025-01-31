package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.Category;
import com.tuanpham.smart_lib_be.domain.Publisher;
import com.tuanpham.smart_lib_be.domain.Request.PublisherReq;
import com.tuanpham.smart_lib_be.domain.Response.PublisherRes;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.service.CategoryService;
import com.tuanpham.smart_lib_be.service.PublisherService;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class PublisherController {
    private final PublisherService publisherService;


    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @PostMapping("/publishers")
    public ResponseEntity<Publisher> create(@Valid @RequestBody Publisher publisher)
            throws IdInvalidException {
        boolean isExist = this.publisherService.handlePublisherExist(publisher.getName());
        if (isExist) {
            throw new IdInvalidException("Nhà xuất bản đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.publisherService.handleCreatePublisher(publisher));
    }

    @PutMapping("/publishers/{id}")
    public ResponseEntity<Publisher> update(@Valid @RequestBody PublisherReq publisherReq, @PathVariable("id") String id)
            throws IdInvalidException {
        return ResponseEntity.ok().body(this.publisherService.handleUpdatePublisher(publisherReq, id));
    }

    @GetMapping("/publishers/{id}")
    public ResponseEntity<PublisherRes> getPublisherById(@PathVariable("id") String id)
            throws IdInvalidException {
        PublisherRes publisherRes = this.publisherService.handleGetPublisherResById(id);
        if (publisherRes == null) {
            throw new IdInvalidException("Nhà xuất bản không tồn tại");
        }
        return ResponseEntity.ok().body(publisherRes);
    }

    @GetMapping("/publishers")
    public ResponseEntity<ResultPaginationDTO> getAllCategories(
            @Filter Specification<Publisher> spec, Pageable pageable
    ) {
        return ResponseEntity.ok().body(this.publisherService.handleGetAllPublishers(spec, pageable));
    }

    @DeleteMapping("/publishers/{id}")
    public ResponseEntity<String> deletePublisher(@PathVariable("id") String id)
            throws IdInvalidException {
        Publisher publisher = this.publisherService.handleFindPublisherById(id);
        if (publisher == null) {
            throw new IdInvalidException("Nhà xuất bản không tồn tại");
        }
    return ResponseEntity.ok().body("Xóa thành công");
    }

    // import data from excel
    @PostMapping("/publishers/import-excel")
    public ResponseEntity<String> importDataFromExcel(@RequestParam("file") MultipartFile file) throws IOException {
        this.publisherService.saveAllFromExcel(file);
        return ResponseEntity.ok().body("Import thành công");
    }
}
