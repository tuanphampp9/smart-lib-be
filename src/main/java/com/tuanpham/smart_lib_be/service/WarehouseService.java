package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.Category;
import com.tuanpham.smart_lib_be.domain.Request.WarehouseReq;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.domain.Warehouse;
import com.tuanpham.smart_lib_be.mapper.WarehouseMapper;
import com.tuanpham.smart_lib_be.repository.CategoryRepository;
import com.tuanpham.smart_lib_be.repository.WarehouseRepository;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    public WarehouseService(WarehouseRepository warehouseRepository, WarehouseMapper warehouseMapper) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
    }

    public boolean handleWarehouseExist(String name) {
        return this.warehouseRepository.existsByName(name);
    }

    public Warehouse handleCreateWarehouse(Warehouse warehouse) {
        return this.warehouseRepository.save(warehouse);
    }

    public Warehouse handleUpdateWarehouse(WarehouseReq warehouseReq, String id) throws IdInvalidException {
        Warehouse warehouse = this.warehouseRepository.findById(id).orElse(null);
        if (warehouse == null) {
            throw new IdInvalidException("Kho không tồn tại");
        }
        if(this.warehouseRepository.existsByNameAndIdNot(warehouseReq.getName(), id)) {
            throw new IdInvalidException("Kho đã tồn tại");
        }
        this.warehouseMapper.updateWarehouse(warehouse, warehouseReq);
        return this.warehouseRepository.save(warehouse);
    }
    public Warehouse handleFindWarehouseById(String id) {
        return this.warehouseRepository.findById(id).orElse(null);
    }

    public ResultPaginationDTO handleGetAllWarehouses(Specification<Warehouse> spec,
                                                      Pageable pageable) {
        Page<Warehouse> pageWarehouses = this.warehouseRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageWarehouses.getSize());
        meta.setTotal(pageWarehouses.getTotalElements());// amount of elements
        meta.setPages(pageWarehouses.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<Warehouse> listWarehouses = pageWarehouses.getContent().stream().map(
                        c -> c)
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listWarehouses);
        return resultPaginationDTO;
    }

    public void handleDeleteWarehouse(String id) {
        this.warehouseRepository.deleteById(id);
    }
}
