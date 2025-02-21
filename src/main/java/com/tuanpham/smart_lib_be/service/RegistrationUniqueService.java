package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.ImportReceiptDetail;
import com.tuanpham.smart_lib_be.domain.Language;
import com.tuanpham.smart_lib_be.domain.RegistrationUnique;
import com.tuanpham.smart_lib_be.domain.Response.LanguageRes;
import com.tuanpham.smart_lib_be.domain.Response.RegistrationUniqueRes;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.repository.RegistrationUniqueRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistrationUniqueService {
    private final RegistrationUniqueRepository registrationUniqueRepository;
    private final EntityManager entityManager;

    public RegistrationUniqueService(RegistrationUniqueRepository registrationUniqueRepository, EntityManager entityManager) {
        this.registrationUniqueRepository = registrationUniqueRepository;
        this.entityManager = entityManager;
    }

    public String generateNextRegistrationId() {
        String sql = "SELECT COUNT(*) FROM registration_unique";
        Number count = (Number) this.entityManager.createNativeQuery(sql).getSingleResult();
        return String.format("PUB%09d", count.longValue() + 1);
    }
    public RegistrationUnique handleCreateRegistrationUnique(RegistrationUnique registrationUnique) {
        return this.registrationUniqueRepository.save(registrationUnique);
    }

    @Transactional
    public void handleDeleteRegistrationUniqueByImportReceiptDetail(ImportReceiptDetail importReceiptDetail) {
        this.registrationUniqueRepository.deleteByImportReceiptDetail(importReceiptDetail);
    }

    public ResultPaginationDTO handleGetAllRegistrationUniques(Specification<RegistrationUnique> spec,
                                                    Pageable pageable) {
        Page<RegistrationUnique> pageRegistrationUnique = this.registrationUniqueRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageRegistrationUnique.getSize());
        meta.setTotal(pageRegistrationUnique.getTotalElements());// amount of elements
        meta.setPages(pageRegistrationUnique.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<RegistrationUniqueRes> listRegistrationUniques = pageRegistrationUnique.getContent().stream().map(
                        r -> {
                            RegistrationUniqueRes registrationUniqueRes = new RegistrationUniqueRes();
                            registrationUniqueRes.setId(r.getId());
                            registrationUniqueRes.setRegistrationId(r.getRegistrationId());
                            registrationUniqueRes.setCreatedAt(r.getCreatedAt());
                            registrationUniqueRes.setStatus(r.getStatus());
                            registrationUniqueRes.setPublicationName(r.getImportReceiptDetail().getPublication().getName());
                            List<RegistrationUniqueRes.HistoryBorrow> historyBorrows = r.getBorrowSlipDetails().stream().map(h -> {
                                RegistrationUniqueRes.HistoryBorrow historyBorrow = new RegistrationUniqueRes.HistoryBorrow();
                                historyBorrow.setBorrowSlipId(h.getBorrowSlip().getId());
                                historyBorrow.setBorrowDate(h.getBorrowSlip().getBorrowDate());
                                historyBorrow.setReturnDate(h.getBorrowSlip().getReturnDate());
                                historyBorrow.setCardId(h.getBorrowSlip().getCardRead().getCardId());
                                historyBorrow.setNote(h.getBorrowSlip().getNote());
                                historyBorrow.setBorrowSlipStatus(h.getBorrowSlip().getStatus());
                                return historyBorrow;
                            }).collect(Collectors.toList());
                            registrationUniqueRes.setHistoryBorrows(historyBorrows);
                            return registrationUniqueRes;
                        })
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listRegistrationUniques);
        return resultPaginationDTO;
    } 
}
