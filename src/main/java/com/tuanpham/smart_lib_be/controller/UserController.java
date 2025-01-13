package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.CardRead;
import com.tuanpham.smart_lib_be.domain.Request.ReqChangePassword;
import com.tuanpham.smart_lib_be.domain.Response.*;
import com.tuanpham.smart_lib_be.domain.User;
import com.tuanpham.smart_lib_be.mapper.UserMapper;
import com.tuanpham.smart_lib_be.service.CardReaderService;
import com.tuanpham.smart_lib_be.service.EmailService;
import com.tuanpham.smart_lib_be.service.RoleService;
import com.tuanpham.smart_lib_be.service.UserService;
import com.tuanpham.smart_lib_be.util.SecurityUtil;
import com.tuanpham.smart_lib_be.util.annotation.ApiMessage;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final EmailService emailService;
    private final CardReaderService cardReaderService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder,
                          UserMapper userMapper, RoleService roleService,
                          EmailService emailService,
                          CardReaderService cardReaderService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.roleService = roleService;
        this.emailService = emailService;
        this.cardReaderService = cardReaderService;
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User user)
            throws IdInvalidException {
        boolean isExist = this.userService.handleCheckUserExist(user.getEmail());
        if (isExist) {
            throw new IdInvalidException("Email is already exist");
        }
        String newPassword = SecurityUtil.generateSecurePassword();
        user.setActive(true);
        //set role reader
        user.setRole(this.roleService.handleGetRoleById(2));
        //send email to user
        this.emailService.sendEmailCreateCardReader(user.getEmail(), "Thông báo tạo thẻ đọc thành công", "emailNewCardReader",user.getFullName(), newPassword);
        //create password
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        User newUser = this.userService.handleCreateUser(user);

        //create card reader
        CardRead cardRead = new CardRead();
        cardRead.setUser(newUser);
        cardRead.setCardId(this.cardReaderService.generateNextCardId());
        this.cardReaderService.handleCreateCardReader(cardRead);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id)
            throws IdInvalidException {
        User findUser = this.userService.handleGetUser(id);
        if (findUser == null) {
            throw new IdInvalidException("User is not exist");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ResUserDTO> getUser(@PathVariable("id") String id)
            throws IdInvalidException {
        User user = this.userService.handleGetUser(id);
        if (user == null) {
            throw new IdInvalidException("User is not exist");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userMapper.toResUserDTO(user));
    }

    @GetMapping("/users")
    @ApiMessage("Get all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handleGetAllUser(spec, pageable));
    }

    @PutMapping("/users/change-password")
    @ApiMessage("Change password")
    public ResponseEntity<RestResponse<String>> changePassword(@RequestBody ReqChangePassword reqChangePassword)
            throws IdInvalidException
    {
        User user = this.userService.handleGetUserByUsername(reqChangePassword.getEmail());
        if (user == null) {
            throw new IdInvalidException("Tài khoản không tồn tại");
        }
        if (!passwordEncoder.matches(reqChangePassword.getOldPassword(), user.getPassword())) {
            throw new IdInvalidException("Mật khẩu cũ không đúng");
        }
        String hashedPassword = passwordEncoder.encode(reqChangePassword.getNewPassword());
        user.setPassword(hashedPassword);
        this.userService.handleCreateUser(user);
        String message = "Đổi mật khẩu thành công";
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setData(message);
        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateDTO> updateUser(@RequestBody User newUser)
            throws IdInvalidException {
        User findUser = this.userService.handleGetUser(newUser.getId());
        if (findUser == null) {
            throw new IdInvalidException("User is not exist");
        }

        User userResult = this.userService.handleUpdateUser(newUser, findUser);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUpdateDTO(userResult));
    }

}
