package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.CardRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CardReaderRepository extends JpaRepository<CardRead, String>, JpaSpecificationExecutor<CardRead> {
}
