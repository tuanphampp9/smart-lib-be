package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.CartUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartUserRepository extends JpaRepository<CartUser, String> {
    CartUser findByUserIdAndPublicationId(String userId, Long publicationId);

    @Query(value = "select COALESCE(sum(quantity),0) from import_receipt_details where publication_id = ?1", nativeQuery = true)
    Long sumQuantityByPublicationId(Long publicationId);

    @Query(value =
            """
            select count(*) from import_receipt_details inner join
            registration_unique on import_receipt_details.id = registration_unique.import_receipt_detail_id
            where publication_id=?1
            and status = "AVAILABLE"
            """, nativeQuery = true)
    Long countQuantityCanBorrow(Long publicationId);
    @Query(value =
            """
                    select registration_id from import_receipt_details inner join
                                registration_unique on import_receipt_details.id = registration_unique.import_receipt_detail_id
                                where publication_id=?1
                                and status = "AVAILABLE"
                    """, nativeQuery = true)
    List<String> getRegistrationIdsByPublicationId(Long publicationId);
}
