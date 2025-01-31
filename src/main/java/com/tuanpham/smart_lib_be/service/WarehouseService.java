package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.Category;
import com.tuanpham.smart_lib_be.domain.Request.WarehouseReq;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.domain.Response.WarehouseRes;
import com.tuanpham.smart_lib_be.domain.Warehouse;
import com.tuanpham.smart_lib_be.mapper.WarehouseMapper;
import com.tuanpham.smart_lib_be.repository.CategoryRepository;
import com.tuanpham.smart_lib_be.repository.PublicationRepository;
import com.tuanpham.smart_lib_be.repository.WarehouseRepository;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final PublicationRepository publicationRepository;

    public WarehouseService(WarehouseRepository warehouseRepository, WarehouseMapper warehouseMapper, PublicationRepository publicationRepository) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
        this.publicationRepository = publicationRepository;
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
    public WarehouseRes handleGetWarehouseResById(String id) {
        Warehouse warehouse = this.warehouseRepository.findById(id).orElse(null);
        if (warehouse == null) {
            return null;
        }
        int numberOfPublications = this.publicationRepository.countPublicationWarehouse(id);
        WarehouseRes warehouseRes = this.warehouseMapper.toWarehouseRes(warehouse);
        warehouseRes.setNumberOfPublications(numberOfPublications);
        return warehouseRes;
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
        List<WarehouseRes> listWarehouses = pageWarehouses.getContent().stream().map(
                        w -> {
                            WarehouseRes warehouseRes = this.warehouseMapper.toWarehouseRes(w);
                            int numberOfPublications = this.publicationRepository.countPublicationWarehouse(w.getId());
                            warehouseRes.setNumberOfPublications(numberOfPublications);
                            return warehouseRes;
                        })
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listWarehouses);
        return resultPaginationDTO;
    }

    public void handleDeleteWarehouse(String id) {
        this.warehouseRepository.deleteById(id);
    }

    public List<Warehouse> getWarehousesFromExcel(InputStream inputStream) {
        List<Warehouse> listWarehouses = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("warehouses");
            int rowIndex = 0;
            for(Row row : sheet) {
                // skip header
                if(rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                Iterator<Cell> cellIterator = row.cellIterator();
                int cellIndex = 0;
                Warehouse warehouse = new Warehouse();
                while(cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch(cellIndex) {
                        case 0:
                            warehouse.setName(cell.getStringCellValue());
                            break;
                        case 1:
                            warehouse.setType(cell.getStringCellValue());
                            break;
                        case 2:
                            warehouse.setDescription(cell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                    cellIndex++;
                }
                listWarehouses.add(warehouse);
            }
        }catch (Exception e) {
            e.getStackTrace();
        }
        return listWarehouses;
    }

    public void saveAllFromExcel(MultipartFile file) {
        if(ExcelService.isValidateExcelFile(file)) {
            try {
                List<Warehouse> listWarehouses = getWarehousesFromExcel(file.getInputStream());
                this.warehouseRepository.saveAll(listWarehouses);
            }catch (IOException e) {
                throw new IllegalArgumentException("Lỗi khi đọc file excel");
            }
        }
    }
}
