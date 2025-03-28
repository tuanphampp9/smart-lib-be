package com.tuanpham.smart_lib_be.specifications;

import com.tuanpham.smart_lib_be.domain.Notifications;
import org.springframework.data.jpa.domain.Specification;

public class NotificationSpecifications {
    public static Specification<Notifications> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("email"), email);
    }
}
