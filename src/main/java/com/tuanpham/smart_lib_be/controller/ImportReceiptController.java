package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.*;
import com.tuanpham.smart_lib_be.domain.Request.ImportReceiptReq;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.service.ImportReceiptDetailService;
import com.tuanpham.smart_lib_be.service.ImportReceiptService;
import com.tuanpham.smart_lib_be.service.PublicationService;
import com.tuanpham.smart_lib_be.service.RegistrationUniqueService;
import com.tuanpham.smart_lib_be.util.constant.PublicationStatusEnum;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ImportReceiptController {
    private final ImportReceiptService importReceiptService;
    private final ImportReceiptDetailService importReceiptDetailService;
    private final PublicationService publicationService;
    private final RegistrationUniqueService registrationUniqueService;
    public ImportReceiptController(ImportReceiptService importReceiptService, ImportReceiptDetailService importReceiptDetailService,
                                   PublicationService publicationService,
                                   RegistrationUniqueService registrationUniqueService) {
        this.importReceiptService = importReceiptService;
        this.importReceiptDetailService = importReceiptDetailService;
        this.publicationService = publicationService;
        this.registrationUniqueService = registrationUniqueService;
    }

    //create import receipt and import receipt detail
    @PostMapping("/import-receipts")
    public ResponseEntity<ImportReceipt> create(@Valid @RequestBody ImportReceiptReq importReceiptReq)
            throws IdInvalidException {
        ImportReceipt importReceipt = new ImportReceipt();
        importReceipt.setInputSource(importReceiptReq.getInputSource());
        importReceipt.setDeliveryPerson(importReceiptReq.getDeliveryPerson());
        importReceipt.setDeliveryRepresentative(importReceiptReq.getDeliveryRepresentative());
        importReceipt.setNote(importReceiptReq.getNote());
        //create import receipt
        this.importReceiptService.createImportReceipt(importReceipt);

        //create list of import receipt detail
        List<ImportReceiptDetail> importReceiptDetails = new ArrayList<>();
        //create import receipt detail
        //loop list of import receipt req
        for (int i = 0; i < importReceiptReq.getImportReceiptDetailReqs().toArray().length; i++) {
            //find publication by id
            Publication publication = this.publicationService.handleFindPublicationById(importReceiptReq.getImportReceiptDetailReqs().get(i).getPublicationId());
            if (publication != null) {
                ImportReceiptDetail importReceiptDetail = new ImportReceiptDetail();
                importReceiptDetail.setImportReceipt(importReceipt);
                importReceiptDetail.setPublication(publication);
                importReceiptDetail.setPrice(importReceiptReq.getImportReceiptDetailReqs().get(i).getPrice());
                importReceiptDetail.setQuantity(importReceiptReq.getImportReceiptDetailReqs().get(i).getQuantity());
                this.importReceiptDetailService.handleCreateImportReceiptDetail(importReceiptDetail);

                //create a list of registration unique
                List<RegistrationUnique> registrationUniques = new ArrayList<>();
                //create registration unique
                //loop quantity of publication
                for (int j = 0; j < importReceiptReq.getImportReceiptDetailReqs().get(i).getQuantity(); j++) {
                    RegistrationUnique registrationUnique = new RegistrationUnique();
                    registrationUnique.setImportReceiptDetail(importReceiptDetail);
                    registrationUnique.setStatus(PublicationStatusEnum.AVAILABLE);
                    //generate registration id
                    registrationUnique.setRegistrationId(this.registrationUniqueService.generateNextRegistrationId());
                    this.registrationUniqueService.handleCreateRegistrationUnique(registrationUnique);
                    registrationUniques.add(registrationUnique);
                }
                importReceiptDetail.setRegistrationUniques(registrationUniques);
                importReceiptDetails.add(importReceiptDetail);
            }
        }
        importReceipt.setImportReceiptDetails(importReceiptDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(importReceipt);
    }

    //get a list of import receipt
    @GetMapping("/import-receipts")
    public ResponseEntity<ResultPaginationDTO> getAllImportReceipts(@Filter Specification<ImportReceipt> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.importReceiptService.handleGetAllImportReceipts(spec, pageable));
    }

    //get import receipt by id
    @GetMapping("/import-receipts/{id}")
    public ResponseEntity<ImportReceipt> getImportReceiptById(@PathVariable("id") Long id)
            throws IdInvalidException {
        ImportReceipt importReceipt = this.importReceiptService.handleFindImportReceiptById(id);
        if (importReceipt == null) {
            throw new IdInvalidException("Phiếu nhập ấn phẩm không tồn tại");
        }
        return ResponseEntity.ok().body(importReceipt);
    }

    //update import receipt by id
    @PutMapping("/import-receipts/{id}")
    public ResponseEntity<ImportReceipt> updateImportReceiptById(@PathVariable("id") Long id, @Valid @RequestBody ImportReceiptReq importReceiptReq)
            throws IdInvalidException {
        //find import receipt by id
        ImportReceipt importReceipt = this.importReceiptService.handleFindImportReceiptById(id);
        if (importReceipt == null) {
            throw new IdInvalidException("Phiếu nhập ấn phẩm không tồn tại");
        }
        importReceipt.setInputSource(importReceiptReq.getInputSource());
        importReceipt.setDeliveryPerson(importReceiptReq.getDeliveryPerson());
        importReceipt.setDeliveryRepresentative(importReceiptReq.getDeliveryRepresentative());
        importReceipt.setNote(importReceiptReq.getNote());

        //delete all registration unique of each import receipt detail
        for (ImportReceiptDetail importReceiptDetail : importReceipt.getImportReceiptDetails()) {
            this.registrationUniqueService.handleDeleteRegistrationUniqueByImportReceiptDetail(importReceiptDetail);
        }
        // delete all import receipt detail of import receipt
        for (ImportReceiptDetail importReceiptDetail : importReceipt.getImportReceiptDetails()) {
            this.importReceiptDetailService.handleDeleteImportReceiptDetailByImportReceipt(importReceiptDetail.getImportReceipt());
        }

        //create list of import receipt detail
        List<ImportReceiptDetail> importReceiptDetails = new ArrayList<>();
        //create import receipt detail
        //loop list of import receipt req
        for (int i = 0; i < importReceiptReq.getImportReceiptDetailReqs().toArray().length; i++) {
            //find publication by id
            Publication publication = this.publicationService.handleFindPublicationById(importReceiptReq.getImportReceiptDetailReqs().get(i).getPublicationId());
            if (publication != null) {
                ImportReceiptDetail importReceiptDetail = new ImportReceiptDetail();
                importReceiptDetail.setImportReceipt(importReceipt);
                importReceiptDetail.setPublication(publication);
                importReceiptDetail.setPrice(importReceiptReq.getImportReceiptDetailReqs().get(i).getPrice());
                importReceiptDetail.setQuantity(importReceiptReq.getImportReceiptDetailReqs().get(i).getQuantity());
                this.importReceiptDetailService.handleCreateImportReceiptDetail(importReceiptDetail);

                //create a list of registration unique
                List<RegistrationUnique> registrationUniques = new ArrayList<>();
                //create registration unique
                //loop quantity of publication
                for (int j = 0; j < importReceiptReq.getImportReceiptDetailReqs().get(i).getQuantity(); j++) {
                    RegistrationUnique registrationUnique = new RegistrationUnique();
                    registrationUnique.setImportReceiptDetail(importReceiptDetail);
                    registrationUnique.setStatus(PublicationStatusEnum.AVAILABLE);
                    //generate registration id
                    registrationUnique.setRegistrationId(this.registrationUniqueService.generateNextRegistrationId());
                    this.registrationUniqueService.handleCreateRegistrationUnique(registrationUnique);
                    registrationUniques.add(registrationUnique);
                }
                importReceiptDetail.setRegistrationUniques(registrationUniques);
                importReceiptDetails.add(importReceiptDetail);
            }
        }
        importReceipt.setImportReceiptDetails(importReceiptDetails);
        this.importReceiptService.createImportReceipt(importReceipt);
        return ResponseEntity.status(HttpStatus.OK).body(importReceipt);
    }
}
