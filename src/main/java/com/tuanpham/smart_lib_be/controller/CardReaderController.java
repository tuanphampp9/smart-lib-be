package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.CardRead;
import com.tuanpham.smart_lib_be.domain.Request.ReqCreateCardReader;
import com.tuanpham.smart_lib_be.domain.Response.ResCreateUserDTO;
import com.tuanpham.smart_lib_be.domain.User;
import com.tuanpham.smart_lib_be.service.CardReaderService;
import com.tuanpham.smart_lib_be.service.EmailService;
import com.tuanpham.smart_lib_be.service.RoleService;
import com.tuanpham.smart_lib_be.service.UserService;
import com.tuanpham.smart_lib_be.util.SecurityUtil;
import com.tuanpham.smart_lib_be.util.annotation.ApiMessage;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CardReaderController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RoleService roleService;
    private final CardReaderService cardReaderService;
    public CardReaderController(UserService userService, PasswordEncoder passwordEncoder,
                                EmailService emailService,
                                RoleService roleService,
                                CardReaderService cardReaderService
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.roleService = roleService;
        this.cardReaderService = cardReaderService;
    }

    @PostMapping("/create-card-reader")
    @ApiMessage("Create new card reader")
    public ResponseEntity<CardRead> createCardReader(@Valid @RequestBody ReqCreateCardReader req)
            throws IdInvalidException {
        User user = this.userService.handleGetUserByUsername(req.getEmail());
        String newPassword = SecurityUtil.generateSecurePassword();
        if (user==null) {
            throw new IdInvalidException("Email is not exist");
        }
        //send email to user
        this.emailService.sendEmailCreateCardReader(user.getEmail(), "Thông báo tạo thẻ đọc thành công", "emailNewCardReader",user.getFullName(), newPassword);
        //create password
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        //active account
        user.setActive(true);
        //save user
        User newUser = this.userService.handleCreateUser(user);
        //create card reader
        CardRead cardRead = new CardRead();
        cardRead.setUser(newUser);
        cardRead.setCardId(this.cardReaderService.generateNextCardId());
        return ResponseEntity.status(HttpStatus.CREATED).body(this.cardReaderService.handleCreateCardReader(cardRead));
    }
}
