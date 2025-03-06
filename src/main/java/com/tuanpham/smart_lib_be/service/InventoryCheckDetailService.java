package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.InventoryCheck;
import com.tuanpham.smart_lib_be.domain.InventoryCheckDetail;
import com.tuanpham.smart_lib_be.domain.RegistrationUnique;
import com.tuanpham.smart_lib_be.domain.Request.InventoryCheckDetailCreate;
import com.tuanpham.smart_lib_be.repository.InventoryCheckDetailRepository;
import com.tuanpham.smart_lib_be.repository.InventoryCheckRepository;
import com.tuanpham.smart_lib_be.repository.RegistrationUniqueRepository;
import com.tuanpham.smart_lib_be.util.constant.PublicationStatusEnum;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryCheckDetailService {
    private final InventoryCheckDetailRepository inventoryCheckDetailRepository;
    private final InventoryCheckRepository inventoryCheckRepository;
    private final RegistrationUniqueRepository registrationUniqueRepository;
    public InventoryCheckDetailService(InventoryCheckDetailRepository inventoryCheckDetailRepository,
                                       InventoryCheckRepository inventoryCheckRepository, RegistrationUniqueRepository registrationUniqueRepository) {
        this.inventoryCheckDetailRepository = inventoryCheckDetailRepository;
        this.inventoryCheckRepository = inventoryCheckRepository;
        this.registrationUniqueRepository = registrationUniqueRepository;
    }

    public InventoryCheck handleCreateInventoryCheckDetail(List<InventoryCheckDetailCreate> inventoryCheckDetailCreates, Long inventoryId) throws IdInvalidException {
        InventoryCheck inventoryCheck = this.inventoryCheckRepository.findById(inventoryId).orElse(null);
        if (inventoryCheck == null) {
            throw new IdInvalidException("Bản kiểm kê không tồn tại");
        }
        List<InventoryCheckDetail> inventoryCheckDetails = this.inventoryCheckDetailRepository.findByInventoryCheck(inventoryCheck);
        //delete all old inventory check detail
        inventoryCheckDetails.forEach(
                inventoryCheckDetail -> {
                    RegistrationUnique registrationUnique = inventoryCheckDetail.getRegistrationUnique();
                    //update registration unique status
                    registrationUnique.setStatus(PublicationStatusEnum.AVAILABLE);
                    this.registrationUniqueRepository.save(registrationUnique);
                    this.inventoryCheckDetailRepository.delete(inventoryCheckDetail);
                }
        );

        //create new list inventory check detail
        List<InventoryCheckDetail> listInventoryCheckDetailNew = new ArrayList<>();
        inventoryCheckDetailCreates.forEach(inventoryCheckDetail -> {
            RegistrationUnique registationUniqueExist = this.registrationUniqueRepository.findByRegistrationId(inventoryCheckDetail.getRegistrationId());
            //update registration unique status
            registationUniqueExist.setStatus(inventoryCheckDetail.getStatus());
            this.registrationUniqueRepository.save(registationUniqueExist);
            InventoryCheckDetail detail = new InventoryCheckDetail();
            detail.setRegistrationUnique(registationUniqueExist);
            detail.setInventoryCheck(inventoryCheck);
            detail.setNote(inventoryCheckDetail.getNote());
            this.inventoryCheckDetailRepository.save(detail);
            //push inventory check detail to inventory check
            listInventoryCheckDetailNew.add(detail);
        });
        inventoryCheck.setInventoryCheckDetails(listInventoryCheckDetailNew);
        return inventoryCheck;
    }
}
