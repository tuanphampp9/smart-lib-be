package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.Liquidation;
import com.tuanpham.smart_lib_be.domain.Notifications;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.service.NotificationNormalService;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class NotificationNormalController {
    private final NotificationNormalService notificationNormalService;

    public NotificationNormalController(NotificationNormalService notificationNormalService) {
        this.notificationNormalService = notificationNormalService;
    }

    //get list notification by user
    @GetMapping("/notifications/user")
    public ResponseEntity<ResultPaginationDTO> getAllNotificationsByUser(
            @Filter Specification<Notifications> spec, Pageable pageable
    ) {
        return ResponseEntity.ok().body(this.notificationNormalService.handleGetAllNotificationsByUser(spec, pageable));
    }
    //get number of notifications unread
    @GetMapping("/notifications/unread")
    public ResponseEntity<Long> getNumberNotificationsUnread() {
        return ResponseEntity.ok().body(this.notificationNormalService.handleGetNumberNotificationsUnread());
    }

    //update status read notification
    @PutMapping("/notifications/read/{id}")
    public ResponseEntity<Boolean> updateStatusReadNotification(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(this.notificationNormalService.handleUpdateStatusReadNotification(id));
    }
}
