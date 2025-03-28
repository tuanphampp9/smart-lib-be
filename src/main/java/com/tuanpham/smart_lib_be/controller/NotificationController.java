package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.Notifications;
import com.tuanpham.smart_lib_be.domain.Request.NotificationReq;
import com.tuanpham.smart_lib_be.domain.Response.NotficationRes;
import com.tuanpham.smart_lib_be.domain.User;
import com.tuanpham.smart_lib_be.repository.NotificationNormalRepository;
import com.tuanpham.smart_lib_be.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class NotificationController {
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final NotificationNormalRepository notificationNormalRepository;
    public NotificationController(SimpMessagingTemplate messagingTemplate, UserService userService, NotificationNormalRepository notificationNormalRepository) {
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.notificationNormalRepository = notificationNormalRepository;
    }
    @MessageMapping("/send-notification")// Lắng nghe từ `/app/send-notification/{id}`
    @SendTo("/topic/notifications") // Gửi tin nhắn đến tất cả client subscribe vào "/topic/notifications"
    public NotficationRes sendNotification(@RequestBody NotificationReq notificationReq) {
        NotficationRes notificationRes = new NotficationRes();
        notificationRes.setId(notificationReq.getId());
        notificationRes.setTitle(notificationReq.getTitle());
        notificationRes.setType(notificationReq.getType());
        //save notification to database
        List<User> users = this.userService.getListUsersActive();

        //create notification for each user
        List<Notifications> notifications = users.stream().map(user -> {
            Notifications notification = new Notifications();
            notification.setUser(user);
            notification.setMessage(notificationReq.getTitle());
            notification.setType(notificationReq.getType());
            notification.setIdDetail(notificationReq.getId());
            return notification;
        }).toList();
        //save all notifications
        this.notificationNormalRepository.saveAll(notifications);
        return notificationRes;
    }
}
