package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.InventoryCheck;
import com.tuanpham.smart_lib_be.domain.Liquidation;
import com.tuanpham.smart_lib_be.domain.Request.InventoryCheckRequest;
import com.tuanpham.smart_lib_be.domain.Request.LiquidationCrqReq;
import com.tuanpham.smart_lib_be.domain.Request.LiquidationDetailCreate;
import com.tuanpham.smart_lib_be.domain.Request.LiquidationReq;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.service.LiquidationDetailService;
import com.tuanpham.smart_lib_be.service.LiquidationService;
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
public class LiquidationController {
    private final LiquidationService liquidationService;
    private final LiquidationDetailService liquidationDetailService;
    public LiquidationController(LiquidationService liquidationService, LiquidationDetailService liquidationDetailService) {
        this.liquidationService = liquidationService;
        this.liquidationDetailService = liquidationDetailService;
    }

    //create new liquidation
    @PostMapping("/liquidations")
    public ResponseEntity<Liquidation> create(@Valid @RequestBody LiquidationCrqReq liquidationCrqReq)
            throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.liquidationService.handleCreateLiquidation(liquidationCrqReq));
    }

    //update liquidation
    @PutMapping("/liquidations/{id}")
    public ResponseEntity<Liquidation> update(@Valid @RequestBody LiquidationReq liquidationReq, @PathVariable("id") Long id)
            throws IdInvalidException {
        return ResponseEntity.ok().body(this.liquidationService.handleUpdateLiquidation(liquidationReq, id));
    }

    //api get liquidations
    @GetMapping("/liquidations")
    public ResponseEntity<ResultPaginationDTO> getAllLiquidation(
            @Filter Specification<Liquidation> spec, Pageable pageable
    ) {
        return ResponseEntity.ok().body(this.liquidationService.handleGetAllLiquidations(spec, pageable));
    }

    //api get liquidation by id
    @GetMapping("/liquidations/{id}")
    public ResponseEntity<Liquidation> getLiquidationById(@PathVariable("id") Long id)
            throws IdInvalidException {
        Liquidation liquidation = this.liquidationService.handleFindLiquidationById(id);
        if (liquidation == null) {
            throw new IdInvalidException("Phiếu thanh lý không tồn tại");
        }
        return ResponseEntity.ok().body(liquidation);
    }

    //api create liquidation detail
    @PostMapping("/liquidations/{id}/liquidation-details")
    public ResponseEntity<Liquidation> createLiquidationDetail(@Valid @RequestBody List<LiquidationDetailCreate> liquidationDetailCreates, @PathVariable("id") Long id)
            throws IdInvalidException {
        return ResponseEntity.ok().body(this.liquidationDetailService.handleCreateLiquidationDetail(liquidationDetailCreates, id));
    }
}
