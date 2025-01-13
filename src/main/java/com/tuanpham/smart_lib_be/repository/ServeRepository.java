package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.CardRead;
import com.tuanpham.smart_lib_be.domain.Serve;
import com.tuanpham.smart_lib_be.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServeRepository extends JpaRepository<Serve, String>, JpaSpecificationExecutor<Serve> {
    Optional<Serve> findByCardReadAndStatus(CardRead cardRead, String status);
}
