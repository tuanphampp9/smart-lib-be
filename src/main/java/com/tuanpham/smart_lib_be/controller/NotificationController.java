package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.Request.NotificationReq;
import com.tuanpham.smart_lib_be.domain.Response.NotficationRes;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class NotificationController {
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    @MessageMapping("/send-notification")// Lắng nghe từ `/app/send-notification/{id}`
    @SendTo("/topic/notifications") // Gửi tin nhắn đến tất cả client subscribe vào "/topic/notifications"
    public NotficationRes sendNotification(@RequestBody NotificationReq notificationReq) {
        NotficationRes notificationRes = new NotficationRes();
        notificationRes.setId(notificationReq.getId());
        notificationRes.setTitle(notificationReq.getTitle());
        notificationRes.setType(notificationReq.getType());
        return notificationRes;
    }
}
