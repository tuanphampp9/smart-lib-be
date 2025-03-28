package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.Request.WarehouseReq;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.domain.Response.WarehousePub;
import com.tuanpham.smart_lib_be.domain.Response.WarehouseRes;
import com.tuanpham.smart_lib_be.domain.Warehouse;
import com.tuanpham.smart_lib_be.service.WarehouseService;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class WarehouseController {
    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping("/warehouses")
    public ResponseEntity<Warehouse> create(@Valid @RequestBody Warehouse warehouse)
            throws IdInvalidException {
        boolean isExist = this.warehouseService.handleWarehouseExist(warehouse.getName());
        if (isExist) {
            throw new IdInvalidException("Kho đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.warehouseService.handleCreateWarehouse(warehouse));
    }

    @PutMapping("/warehouses/{id}")
    public ResponseEntity<Warehouse> update(@Valid @RequestBody WarehouseReq warehouseReq, @PathVariable("id") String id)
            throws IdInvalidException {
        return ResponseEntity.ok().body(this.warehouseService.handleUpdateWarehouse(warehouseReq, id));
    }

    @GetMapping("/warehouses/{id}")
    public ResponseEntity<WarehouseRes> getWarehouseById(@PathVariable("id") String id)
            throws IdInvalidException {
        WarehouseRes warehouseRes = this.warehouseService.handleGetWarehouseResById(id);
        if (warehouseRes == null) {
            throw new IdInvalidException("Kho không tồn tại");
        }
        return ResponseEntity.ok().body(warehouseRes);
    }

    @GetMapping("/warehouses")
    public ResponseEntity<ResultPaginationDTO> getAllWarehouses(
            @Filter Specification<Warehouse> spec, Pageable pageable
    ) {
        return ResponseEntity.ok().body(this.warehouseService.handleGetAllWarehouses(spec, pageable));
    }

    @DeleteMapping("/warehouses/{id}")
    public ResponseEntity<String> deleteWarehouse(@PathVariable("id") String id)
            throws IdInvalidException {
        Warehouse warehouse = this.warehouseService.handleFindWarehouseById(id);
        if (warehouse == null) {
            throw new IdInvalidException("Kho không tồn tại");
        }
        this.warehouseService.handleDeleteWarehouse(id);
        return ResponseEntity.ok().body("Xóa kho thành công");
    }

    // import data from excel
    @PostMapping("/warehouses/import-excel")
    public ResponseEntity<String> importDataFromExcel(@RequestParam("file") MultipartFile file) throws IOException {
        this.warehouseService.saveAllFromExcel(file);
        return ResponseEntity.ok().body("Import thành công");
    }
    //get all warehouses
    @GetMapping("/warehouses/list-publications")
    public ResponseEntity<List<WarehousePub>> getAllWarehousesWithPublications(

    ) {
        return ResponseEntity.ok().body(this.warehouseService.handleGetAllWarehousesWithPublications());
    }
}
