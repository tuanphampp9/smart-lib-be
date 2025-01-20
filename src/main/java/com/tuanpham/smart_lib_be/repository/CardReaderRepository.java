package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.CardRead;
import com.tuanpham.smart_lib_be.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardReaderRepository extends JpaRepository<CardRead, String>, JpaSpecificationExecutor<CardRead> {
    Optional<CardRead> findByCardId(String cardId);

}
