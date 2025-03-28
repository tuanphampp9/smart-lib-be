package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface NotificationNormalRepository extends JpaRepository<Notifications, Long>, JpaSpecificationExecutor<Notifications> {
    @Query(value = """
            SELECT COALESCE(count(*),0) FROM notifications
            where notifications.user_id=?1
            and is_read=?2
            """, nativeQuery = true)
    Long countByUserIdAndReadStatus(String userId, boolean readStatus);
}
