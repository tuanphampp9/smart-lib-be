package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.InventoryCheck;
import com.tuanpham.smart_lib_be.domain.Liquidation;
import com.tuanpham.smart_lib_be.domain.Request.LiquidationCrqReq;
import com.tuanpham.smart_lib_be.domain.Request.LiquidationDetailCreate;
import com.tuanpham.smart_lib_be.domain.Request.LiquidationReq;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.domain.User;
import com.tuanpham.smart_lib_be.mapper.LiquidationMapper;
import com.tuanpham.smart_lib_be.repository.LiquidationRepository;
import com.tuanpham.smart_lib_be.repository.UserRepository;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LiquidationService {
    private final LiquidationRepository liquidationRepository;
    private final LiquidationMapper liquidationMapper;
    private final UserRepository userRepository;
    public LiquidationService(LiquidationRepository liquidationRepository, LiquidationMapper liquidationMapper,
                              UserRepository userRepository) {
        this.liquidationRepository = liquidationRepository;
        this.liquidationMapper = liquidationMapper;
        this.userRepository = userRepository;
    }
    
    public Liquidation handleCreateLiquidation(LiquidationCrqReq liquidationCrqReq) throws IdInvalidException {
        Liquidation liquidationToSave = new Liquidation();
        liquidationToSave.setReceiverName(liquidationCrqReq.getReceiverName());
        liquidationToSave.setReceiverContact(liquidationCrqReq.getReceiverContact());
        liquidationToSave.setNote(liquidationCrqReq.getNote());
        User user = this.userRepository.findById(liquidationCrqReq.getUserId()).orElse(null);
        if(user == null) {
            throw new IdInvalidException("Người dùng không tồn tại");
        }
        liquidationToSave.setUser(user);
        return this.liquidationRepository.save(liquidationToSave);
    }
    
    public Liquidation handleUpdateLiquidation(LiquidationReq liquidationReq, Long id) throws IdInvalidException {
        Liquidation liquidationToUpdate = this.liquidationRepository.findById(id).orElse(null);
        if (liquidationToUpdate == null) {
            throw new IdInvalidException("Phiếu thanh lý không tồn tại");
        }
        this.liquidationMapper.updateLiquidation(liquidationToUpdate, liquidationReq);
        return this.liquidationRepository.save(liquidationToUpdate);
    }

    public ResultPaginationDTO handleGetAllLiquidations(Specification<Liquidation> spec, Pageable pageable) {
        Page<Liquidation> pageLiquidations = this.liquidationRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageLiquidations.getSize());
        meta.setTotal(pageLiquidations.getTotalElements());// amount of elements
        meta.setPages(pageLiquidations.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<Liquidation> listInventoryChecks = pageLiquidations.getContent().stream().map(
                        l -> l)
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listInventoryChecks);
        return resultPaginationDTO;
    }

    public Liquidation handleFindLiquidationById(Long id) {
        return this.liquidationRepository.findById(id).orElse(null);
    }


}
