package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.service.EmailService;
import com.tuanpham.smart_lib_be.util.annotation.ApiMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class EmailController {

    private final EmailService emailService;
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/email")
    @ApiMessage("Send simple email")
    // @Scheduled(cron = "*/60 * * * * *")
    // @Transactional
    public String sendSimpleEmail() {
//         this.emailService.sendSimpleEmail();
        // this.emailService.sendEmailSync("tuanphampp9@gmail.com", "test thymeleaf",
        // "<h1>check hello</h1>", false,
        // true);
         //this.emailService.sendEmailFromTemplateSync("tuanphampp9@gmail.com", "Thông báo mật khẩu mới của bạn đọc", "acceptAccount","tuan pham");
        return "Email sent successfully";
    }

}
