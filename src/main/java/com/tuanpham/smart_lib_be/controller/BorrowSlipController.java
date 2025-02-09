package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.*;
import com.tuanpham.smart_lib_be.domain.Request.BorrowSlipAdminReq;
import com.tuanpham.smart_lib_be.domain.Request.BorrowSlipClientReq;
import com.tuanpham.smart_lib_be.domain.Request.ReturnBorrowSlipReq;
import com.tuanpham.smart_lib_be.domain.Response.BorrowSlipRes;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.service.BorrowSlipService;
import com.tuanpham.smart_lib_be.service.CardReaderService;
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
public class BorrowSlipController {
    private final BorrowSlipService borrowSlipService;
    private final CardReaderService cardReaderService;

    public BorrowSlipController(BorrowSlipService borrowSlipService, CardReaderService cardReaderService) {
        this.borrowSlipService = borrowSlipService;
        this.cardReaderService = cardReaderService;
    }

    // create new borrow slip
    @PostMapping("/client/borrow-slips")
    public ResponseEntity<BorrowSlip> create(@Valid @RequestBody BorrowSlipClientReq borrowSlipClientReq)
            throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.borrowSlipService.handleCreateBorrowSlipForClient(borrowSlipClientReq));
    }

    // get borrow slips
    @GetMapping("/borrow-slips")
    public ResponseEntity<ResultPaginationDTO> getAllBorrowSlips(
            @Filter Specification<BorrowSlip> spec, Pageable pageable
    ) {
        return ResponseEntity.ok().body(this.borrowSlipService.handleGetAllBorrowSlips(spec, pageable));
    }

    // accept borrow slip
    @PutMapping("/borrow-slips/{borrowSlipId}/accept")
    public ResponseEntity<BorrowSlip> acceptBorrowSlip(@PathVariable String borrowSlipId) throws IdInvalidException {
        return ResponseEntity.ok().body(this.borrowSlipService.handleAcceptBorrowSlip(borrowSlipId));
    }

    //delete borrow slip
    @DeleteMapping("/borrow-slips/{borrowSlipId}")
    public ResponseEntity<String> deleteBorrowSlip(@PathVariable String borrowSlipId) throws IdInvalidException {
        this.borrowSlipService.handleDeleteBorrowSlip(borrowSlipId);
        return ResponseEntity.ok().body("Delete borrow slip successfully");
    }

    //create borrow slip by admin
    @PostMapping("/admin/borrow-slips")
    public ResponseEntity<BorrowSlip> createBorrowSlipByAdmin(@Valid @RequestBody BorrowSlipAdminReq borrowSlip) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.borrowSlipService.handleCreateBorrowSlipByAdmin(borrowSlip));
    }

    // return borrow slip
    @PutMapping("/borrow-slips/{borrowSlipId}/return")
    public ResponseEntity<BorrowSlip> returnBorrowSlip(@Valid @RequestBody ReturnBorrowSlipReq returnBorrowSlipReq) throws IdInvalidException {
        return ResponseEntity.ok().body(this.borrowSlipService.handleReturnBorrowSlip(returnBorrowSlipReq));
    }

    // get borrow slip by id
    @GetMapping("/borrow-slips/{borrowSlipId}")
    public ResponseEntity<BorrowSlipRes> getBorrowSlipById(@PathVariable String borrowSlipId) throws IdInvalidException {
        return ResponseEntity.ok().body(this.borrowSlipService.handleGetBorrowSlipById(borrowSlipId));
    }

}
