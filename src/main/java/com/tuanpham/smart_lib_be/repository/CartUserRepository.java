package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.CartUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartUserRepository extends JpaRepository<CartUser, String> {
    CartUser findByUserIdAndPublicationId(String userId, Long publicationId);
}
