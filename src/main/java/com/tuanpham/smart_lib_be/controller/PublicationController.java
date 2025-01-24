package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.Publication;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.service.PublicationService;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PublicationController {
    private final PublicationService publicationService;

    public PublicationController(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

    @PostMapping("/publications")
    public ResponseEntity<Publication> create(@Valid @RequestBody Publication publication)
            throws IdInvalidException {
        boolean isExist = this.publicationService.handlePublicationExist(publication.getName());
        if (isExist) {
            throw new IdInvalidException("Ấn phẩm đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.publicationService.handleCreatePublication(publication));
    }

    @PutMapping("/publications/{id}")
    public ResponseEntity<Publication> update(@Valid @RequestBody Publication publication, @PathVariable("id") Long id)
            throws IdInvalidException {
        return ResponseEntity.ok().body(this.publicationService.handleUpdatePublication(publication, id));
    }

    @GetMapping("/publications/{id}")
    public ResponseEntity<Publication> getPublicationById(@PathVariable("id") Long id)
            throws IdInvalidException {
        Publication publication = this.publicationService.handleFindPublicationById(id);
        if (publication == null) {
            throw new IdInvalidException("Ấn phẩm không tồn tại");
        }
        return ResponseEntity.ok().body(publication);
    }

    @GetMapping("/publications")
    public ResponseEntity<ResultPaginationDTO> getAllPublications(
            @Filter Specification<Publication> spec, Pageable pageable
    ) {

        return ResponseEntity.ok().body(this.publicationService.handleGetAlLPublication(spec, pageable));
    }

    @DeleteMapping("/publications/{id}")
    public ResponseEntity<String> deletePublication(@PathVariable("id") Long id)
            throws IdInvalidException {
        Publication publication = this.publicationService.handleFindPublicationById(id);
        if (publication == null) {
            throw new IdInvalidException("Ấn phẩm không tồn tại");
        }
        this.publicationService.handleDeletePublication(id);
        return ResponseEntity.ok().body("Xóa thành công");
    }
}
