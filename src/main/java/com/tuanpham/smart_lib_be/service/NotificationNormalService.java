package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.Notifications;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.domain.User;
import com.tuanpham.smart_lib_be.repository.NotificationNormalRepository;
import com.tuanpham.smart_lib_be.repository.UserRepository;
import com.tuanpham.smart_lib_be.specifications.NotificationSpecifications;
import com.tuanpham.smart_lib_be.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationNormalService {
    private final NotificationNormalRepository notificationNormalRepository;
    private final UserRepository userRepository;

    public NotificationNormalService(NotificationNormalRepository notificationNormalRepository, UserRepository userRepository) {
        this.notificationNormalRepository = notificationNormalRepository;
        this.userRepository = userRepository;
    }

    //get list notification by user
    public ResultPaginationDTO handleGetAllNotificationsByUser(Specification<Notifications> spec,
                                                  Pageable pageable) {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        // Thêm điều kiện lọc theo email vào spec
        Specification<Notifications> finalSpec = spec.and(NotificationSpecifications.hasEmail(email));
        Page<Notifications> pageNotifications = this.notificationNormalRepository.findAll(finalSpec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageNotifications.getSize());
        meta.setTotal(pageNotifications.getTotalElements());// amount of elements
        meta.setPages(pageNotifications.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<Notifications> listNotifications = pageNotifications.getContent().stream().map(
                        n -> {
                            return n;
                        })
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listNotifications);
        return resultPaginationDTO;
    }

    //get number of notifications unread
    public Long handleGetNumberNotificationsUnread() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User user = this.userRepository.findByEmail(email);
        return this.notificationNormalRepository.countByUserIdAndReadStatus(user.getId(), false);
    }

    //update status read notification
    public boolean handleUpdateStatusReadNotification(Long id) {
        Notifications notification = this.notificationNormalRepository.findById(id).orElse(null);
        if (notification == null) {
            return false;
        }
        notification.setRead(true);
        this.notificationNormalRepository.save(notification);
        return true;
    }
}
