package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.CardRead;
import com.tuanpham.smart_lib_be.domain.Permission;
import com.tuanpham.smart_lib_be.domain.Response.ResUserDTO;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.domain.Role;
import com.tuanpham.smart_lib_be.domain.User;
import com.tuanpham.smart_lib_be.repository.CardReaderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardReaderService {
    private final CardReaderRepository cardReaderRepository;
    private EntityManager entityManager;

    public CardReaderService(CardReaderRepository cardReaderRepository,EntityManager entityManager) {
        this.cardReaderRepository = cardReaderRepository;
        this.entityManager = entityManager;
    }
    public CardRead handleCreateCardReader(CardRead cardRead) {
        return this.cardReaderRepository.save(cardRead);
    }

    public CardRead handleGetCardReader(String id) {
        return this.cardReaderRepository.findByCardId(id).orElse(null);
    }

    public String generateNextCardId() {
        String sql = "SELECT COUNT(*) FROM card_reads";
        Number count = (Number) this.entityManager.createNativeQuery(sql).getSingleResult();
        return String.format("THE%06d", count.longValue() + 1);
    }

    public ResultPaginationDTO handleGetAllCardReaders(Specification<CardRead> spec,
                                                  Pageable pageable) {
        Page<CardRead> pageCardRead = this.cardReaderRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageCardRead.getSize());
        meta.setTotal(pageCardRead.getTotalElements());// amount of elements
        meta.setPages(pageCardRead.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<CardRead> listCardReaders = pageCardRead.getContent().stream().map(
                        c -> c)
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listCardReaders);
        return resultPaginationDTO;
    }
}
