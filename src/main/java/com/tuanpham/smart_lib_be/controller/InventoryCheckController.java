package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.Author;
import com.tuanpham.smart_lib_be.domain.InventoryCheck;
import com.tuanpham.smart_lib_be.domain.Language;
import com.tuanpham.smart_lib_be.domain.Request.AuthorReq;
import com.tuanpham.smart_lib_be.domain.Request.InventoryCheckDetailCreate;
import com.tuanpham.smart_lib_be.domain.Request.InventoryCheckRequest;
import com.tuanpham.smart_lib_be.domain.Request.InventoryReqCreate;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.service.InventoryCheckDetailService;
import com.tuanpham.smart_lib_be.service.InventoryCheckService;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class InventoryCheckController {
    private final InventoryCheckService inventoryCheckService;
    private final InventoryCheckDetailService inventoryCheckDetailService;

    public InventoryCheckController(InventoryCheckService inventoryCheckService, InventoryCheckDetailService inventoryCheckDetailService) {
        this.inventoryCheckService = inventoryCheckService;
        this.inventoryCheckDetailService = inventoryCheckDetailService;
    }

    //create new inventory check
    @PostMapping("/inventory-checks")
    public ResponseEntity<InventoryCheck> create(@Valid @RequestBody InventoryReqCreate inventoryReqCreate)
            throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.inventoryCheckService.handleCreateInventoryCheck(inventoryReqCreate));
    }

    //update inventory check
    @PutMapping("/inventory-checks/{id}")
    public ResponseEntity<InventoryCheck> update(@Valid @RequestBody InventoryCheckRequest inventoryCheckRequest, @PathVariable("id") Long id)
            throws IdInvalidException {
        return ResponseEntity.ok().body(this.inventoryCheckService.handleUpdateInventoryCheck(inventoryCheckRequest, id));
    }

    //api get list inventory check
    @GetMapping("/inventory-checks")
    public ResponseEntity<ResultPaginationDTO> getAllInventoryCheck(
            @Filter Specification<InventoryCheck> spec, Pageable pageable
    ) {
        return ResponseEntity.ok().body(this.inventoryCheckService.handleGetAllInventoryCheck(spec, pageable));
    }

    //api get inventory check by id
    @GetMapping("/inventory-checks/{id}")
    public ResponseEntity<InventoryCheck> getInventoryCheckById(@PathVariable("id") Long id)
            throws IdInvalidException {
        InventoryCheck inventoryCheck = this.inventoryCheckService.handleFindInventoryCheckById(id);
        if (inventoryCheck == null) {
            throw new IdInvalidException("Bản kiểm kê không tồn tại");
        }
        return ResponseEntity.ok().body(inventoryCheck);
    }

    //api create inventory check detail
    @PostMapping("/inventory-checks/{id}/details")
    public ResponseEntity<InventoryCheck> createInventoryCheckDetail(@Valid @RequestBody List<InventoryCheckDetailCreate> inventoryCheckDetailCreates, @PathVariable("id") Long id)
            throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.inventoryCheckDetailService.handleCreateInventoryCheckDetail(inventoryCheckDetailCreates, id));
    }

}
