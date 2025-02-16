package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.Post;
import com.tuanpham.smart_lib_be.domain.PublicationRequest;
import com.tuanpham.smart_lib_be.domain.Request.PubReqRes;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.service.PublicationRequestService;
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
public class PublicationRequestController {
    private final PublicationRequestService publicationRequestService;

    public PublicationRequestController(PublicationRequestService publicationRequestService) {
        this.publicationRequestService = publicationRequestService;
    }

    //create publication request
    @PostMapping("/publication-requests")
    public ResponseEntity<PublicationRequest> create(@Valid @RequestBody PublicationRequest publicationRequest)
             {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.publicationRequestService.handleCreatePublicationRequest(publicationRequest));
    }

    //response to publication request
    @PutMapping("/publication-requests/{id}")
    public ResponseEntity<PublicationRequest> update(@Valid @RequestBody PubReqRes pubReqRes, @PathVariable("id") Long id)
            throws IdInvalidException {
        return ResponseEntity.ok().body(this.publicationRequestService.handleUpdatePublicationRequest(pubReqRes, id));
    }

    //get publication requests for admin
    @GetMapping("/publication-requests")
    public ResponseEntity<ResultPaginationDTO> getAllPublicationRequests(@Filter Specification<PublicationRequest> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.publicationRequestService.handleGetAllPublicationRequests(spec, pageable));
    }

    //get publication requests for user
    @GetMapping("/publication-requests/user/{id}")
    public ResponseEntity<ResultPaginationDTO> getAllPublicationRequestsForUser(@PathVariable("id") String id, @Filter Specification<PublicationRequest> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.publicationRequestService.handleGetAllPublicationRequestsForUser(id, spec, pageable));
    }

    //delete publication request
    @DeleteMapping("/publication-requests/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) throws IdInvalidException {
        this.publicationRequestService.handleDeletePublicationRequest(id);
        return ResponseEntity.ok().body("Xóa yêu cầu ấn phẩm thành công");
    }
}
