package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.Liquidation;
import com.tuanpham.smart_lib_be.domain.LiquidationDetail;
import com.tuanpham.smart_lib_be.domain.RegistrationUnique;
import com.tuanpham.smart_lib_be.domain.Request.LiquidationDetailCreate;
import com.tuanpham.smart_lib_be.repository.LiquidationDetailRepository;
import com.tuanpham.smart_lib_be.repository.LiquidationRepository;
import com.tuanpham.smart_lib_be.repository.RegistrationUniqueRepository;
import com.tuanpham.smart_lib_be.util.constant.PublicationStatusEnum;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LiquidationDetailService {
    private final LiquidationDetailRepository liquidationDetailRepository;
    private final LiquidationRepository liquidationRepository;
    private final RegistrationUniqueRepository registrationUniqueRepository;
    public LiquidationDetailService(LiquidationDetailRepository liquidationDetailRepository,
                                    LiquidationRepository liquidationRepository, RegistrationUniqueRepository registrationUniqueRepository) {
        this.liquidationDetailRepository = liquidationDetailRepository;
        this.liquidationRepository = liquidationRepository;
        this.registrationUniqueRepository = registrationUniqueRepository;
    }

    public Liquidation handleCreateLiquidationDetail(List<LiquidationDetailCreate> liquidationDetailCreates, Long id) throws IdInvalidException {
        Liquidation liquidationToUpdate = this.liquidationRepository.findById(id).orElse(null);
        if (liquidationToUpdate == null) {
            throw new IdInvalidException("Phiếu thanh lý không tồn tại");
        }
        List<LiquidationDetail> liquidationDetails = this.liquidationDetailRepository.findByLiquidation(liquidationToUpdate);
        //delete all old liquidation detail
        liquidationDetails.forEach(
                liquidationDetail -> {
                    RegistrationUnique registrationUnique = liquidationDetail.getRegistrationUnique();
                    //update registration unique status
                    registrationUnique.setStatus(PublicationStatusEnum.AVAILABLE);
                    this.registrationUniqueRepository.save(registrationUnique);
                    this.liquidationDetailRepository.delete(liquidationDetail);
                }
        );
        //create new list liquidation detail
        List<LiquidationDetail> listLiquidationDetailNew = new ArrayList<>();
        liquidationDetailCreates.forEach(liquidationDetailCreate -> {
            RegistrationUnique registrationUnique = this.registrationUniqueRepository.findByRegistrationId(liquidationDetailCreate.getRegistrationId());
            //update registration unique status
            registrationUnique.setStatus(PublicationStatusEnum.LIQUIDATED);
            this.registrationUniqueRepository.save(registrationUnique);
            LiquidationDetail liquidationDetail = new LiquidationDetail();
            liquidationDetail.setConditionStatus(liquidationDetailCreate.getConditionStatus());
            liquidationDetail.setLiquidation(liquidationToUpdate);
            liquidationDetail.setPrice(liquidationDetailCreate.getPrice());
            liquidationDetail.setRegistrationUnique(registrationUnique);
            liquidationDetail.setNote(liquidationDetailCreate.getNote());
            this.liquidationDetailRepository.save(liquidationDetail);
            listLiquidationDetailNew.add(liquidationDetail);
        });
        liquidationToUpdate.setLiquidationDetails(listLiquidationDetailNew);
        return this.liquidationRepository.save(liquidationToUpdate);
    }
}
