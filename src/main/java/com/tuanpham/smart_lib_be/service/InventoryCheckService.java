package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.InventoryCheck;
import com.tuanpham.smart_lib_be.domain.Request.InventoryCheckRequest;
import com.tuanpham.smart_lib_be.domain.Request.InventoryReqCreate;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.domain.User;
import com.tuanpham.smart_lib_be.domain.Warehouse;
import com.tuanpham.smart_lib_be.mapper.InventoryCheckMapper;
import com.tuanpham.smart_lib_be.repository.InventoryCheckRepository;
import com.tuanpham.smart_lib_be.repository.UserRepository;
import com.tuanpham.smart_lib_be.repository.WarehouseRepository;
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
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;

    public InventoryCheckService(InventoryCheckRepository inventoryCheckRepository,
                                 InventoryCheckMapper inventoryCheckMapper, UserRepository userRepository,
                                 WarehouseRepository warehouseRepository) {
        this.inventoryCheckRepository = inventoryCheckRepository;
        this.inventoryCheckMapper = inventoryCheckMapper;
        this.userRepository = userRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public InventoryCheck handleCreateInventoryCheck(InventoryReqCreate inventoryReqCreate) throws  IdInvalidException{
        User userExist = this.userRepository.findById(inventoryReqCreate.getUserId()).orElse(null);
        if(userExist == null) {
            throw new IdInvalidException("Người dùng không tồn tại");
        }
        Warehouse warehouseExist = this.warehouseRepository.findById(inventoryReqCreate.getWarehouseId()).orElse(null);
        if(warehouseExist == null) {
            throw new IdInvalidException("Kho không tồn tại");
        }
        InventoryCheck inventoryCheck = new InventoryCheck();
        inventoryCheck.setUser(userExist);
        inventoryCheck.setWarehouse(warehouseExist);
        inventoryCheck.setNote(inventoryReqCreate.getNote());
        return this.inventoryCheckRepository.save(inventoryCheck);
    }

    public InventoryCheck handleUpdateInventoryCheck(InventoryCheckRequest inventoryCheck, Long id) throws IdInvalidException {
        InventoryCheck inventoryCheckToUpdate = this.inventoryCheckRepository.findById(id).orElse(null);
        if (inventoryCheckToUpdate == null) {
            throw new IdInvalidException("Bản ghi kiểm kê không tồn tại");
        }
        Warehouse warehouseExist = this.warehouseRepository.findById(inventoryCheck.getWarehouseId()).orElse(null);
        if(warehouseExist == null) {
            throw new IdInvalidException("Kho không tồn tại");
        }
        this.inventoryCheckMapper.updateInventoryCheck(inventoryCheckToUpdate, inventoryCheck);
        inventoryCheckToUpdate.setWarehouse(warehouseExist);
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
