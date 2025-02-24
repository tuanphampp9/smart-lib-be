package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.InventoryCheck;
import com.tuanpham.smart_lib_be.domain.Request.InventoryCheckRequest;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.mapper.InventoryCheckMapper;
import com.tuanpham.smart_lib_be.repository.InventoryCheckRepository;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryCheckService {
    private final InventoryCheckRepository inventoryCheckRepository;
    private final InventoryCheckMapper inventoryCheckMapper;

    public InventoryCheckService(InventoryCheckRepository inventoryCheckRepository, InventoryCheckMapper inventoryCheckMapper) {
        this.inventoryCheckRepository = inventoryCheckRepository;
        this.inventoryCheckMapper = inventoryCheckMapper;
    }

    public InventoryCheck handleCreateInventoryCheck(InventoryCheck inventoryCheck) {
        return this.inventoryCheckRepository.save(inventoryCheck);
    }

    public InventoryCheck handleUpdateInventoryCheck(InventoryCheckRequest inventoryCheck, Long id) throws IdInvalidException {
        InventoryCheck inventoryCheckToUpdate = this.inventoryCheckRepository.findById(id).orElse(null);
        if (inventoryCheckToUpdate == null) {
            throw new IdInvalidException("Bản ghi kiểm kê không tồn tại");
        }
        this.inventoryCheckMapper.updateInventoryCheck(inventoryCheckToUpdate, inventoryCheck);
        return this.inventoryCheckRepository.save(inventoryCheckToUpdate);
    }

    public ResultPaginationDTO handleGetAllInventoryCheck(Specification<InventoryCheck> spec, Pageable pageable) {
        Page<InventoryCheck> pageInventoryChecks = this.inventoryCheckRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageInventoryChecks.getSize());
        meta.setTotal(pageInventoryChecks.getTotalElements());// amount of elements
        meta.setPages(pageInventoryChecks.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<InventoryCheck> listInventoryChecks = pageInventoryChecks.getContent().stream().map(
                        i -> i)
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listInventoryChecks);
        return resultPaginationDTO;
    }

    public InventoryCheck handleFindInventoryCheckById(Long id) {
        return this.inventoryCheckRepository.findById(id).orElse(null);
    }
}
