package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.PublicationRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicationRatingRepository extends JpaRepository<PublicationRating, String> {
    boolean existsByUserIdAndPublicationId(String userId, Long publicationId);
    PublicationRating findByUserIdAndPublicationId(String userId, Long publicationId);
}
