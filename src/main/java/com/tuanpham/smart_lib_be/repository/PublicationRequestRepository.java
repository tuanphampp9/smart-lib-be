package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.PublicationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PublicationRequestRepository extends JpaRepository<PublicationRequest, Long>, JpaSpecificationExecutor<PublicationRequest> {
}
