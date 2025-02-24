package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.InventoryCheck;
import com.tuanpham.smart_lib_be.domain.InventoryCheckDetail;
import com.tuanpham.smart_lib_be.domain.Request.InventoryCheckDetailCreate;
import com.tuanpham.smart_lib_be.repository.InventoryCheckDetailRepository;
import com.tuanpham.smart_lib_be.repository.InventoryCheckRepository;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryCheckDetailService {
    private final InventoryCheckDetailRepository inventoryCheckDetailRepository;
    private final InventoryCheckRepository inventoryCheckRepository;
    public InventoryCheckDetailService(InventoryCheckDetailRepository inventoryCheckDetailRepository, InventoryCheckRepository inventoryCheckRepository) {
        this.inventoryCheckDetailRepository = inventoryCheckDetailRepository;
        this.inventoryCheckRepository = inventoryCheckRepository;
    }

    public InventoryCheck handleCreateInventoryCheckDetail(InventoryCheckDetailCreate inventoryCheckDetailCreate, Long inventoryId) throws IdInvalidException {
        InventoryCheck inventoryCheck = this.inventoryCheckRepository.findById(inventoryId).orElse(null);
        if (inventoryCheck == null) {
            throw new IdInvalidException("Bản kiểm kê không tồn tại");
        }
        List<InventoryCheckDetail> inventoryCheckDetails = this.inventoryCheckDetailRepository.findByInventoryCheck(inventoryCheck);
        //delete all old inventory check detail
        inventoryCheckDetails.forEach(this.inventoryCheckDetailRepository::delete);

        //create new list inventory check detail
        List<InventoryCheckDetail> listInventoryCheckDetailNew = new ArrayList<>();
        inventoryCheckDetailCreate.getRegistrationUniques().forEach(registrationUnique -> {
            InventoryCheckDetail inventoryCheckDetail = new InventoryCheckDetail();
            inventoryCheckDetail.setRegistrationUnique(registrationUnique);
            inventoryCheckDetail.setInventoryCheck(inventoryCheck);
            this.inventoryCheckDetailRepository.save(inventoryCheckDetail);
            //push inventory check detail to inventory check
            listInventoryCheckDetailNew.add(inventoryCheckDetail);
        });
        inventoryCheck.setInventoryCheckDetails(listInventoryCheckDetailNew);
        return inventoryCheck;
    }
}
