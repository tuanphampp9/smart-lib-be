package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.Request.ReqLoginDTO;
import com.tuanpham.smart_lib_be.domain.Response.ResCreateUserDTO;
import com.tuanpham.smart_lib_be.domain.Response.ResLoginDTO;
import com.tuanpham.smart_lib_be.domain.Response.ResUserDTO;
import com.tuanpham.smart_lib_be.domain.Role;
import com.tuanpham.smart_lib_be.domain.User;
import com.tuanpham.smart_lib_be.mapper.UserMapper;
import com.tuanpham.smart_lib_be.service.EmailService;
import com.tuanpham.smart_lib_be.service.RoleService;
import com.tuanpham.smart_lib_be.service.UserService;
import com.tuanpham.smart_lib_be.util.SecurityUtil;
import com.tuanpham.smart_lib_be.util.annotation.ApiMessage;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final SecurityUtil securityUtil;
        private final UserService userService;
        private final PasswordEncoder passwordEncoder;
        private final UserMapper userMapper;
        private final EmailService emailService;
        private final RoleService roleService;
        @Value("${tuanpp9.jwt.refresh-token-validity-in-seconds}")
        private long refreshTokenExpiration;

        public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
                              SecurityUtil securityUtil,
                              UserMapper userMapper,
                              UserService userService,
                              PasswordEncoder passwordEncoder,
                              RoleService roleService,
                              EmailService emailService) {
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.securityUtil = securityUtil;
                this.userService = userService;
                this.passwordEncoder = passwordEncoder;
                this.userMapper = userMapper;
                this.emailService = emailService;
                this.roleService = roleService;
        }

        @PostMapping("/auth/login")
        public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO)
                        throws IdInvalidException {
                User user = this.userService.handleGetUserByEmail(loginDTO.getUsername());
                if (user==null) {
                        throw new IdInvalidException("username or password is invalid");
                }
                if(!user.isActive()){
                        throw new IdInvalidException("Tài khoản của bạn chưa được kích hoạt");
                }
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                loginDTO.getUsername(), loginDTO.getPassword());

                // authentication user => need write method loadUserByUsername
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);

                // set authentication into security context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                ResLoginDTO resLoginDTO = new ResLoginDTO();
                resLoginDTO.setUser(this.userMapper.toResUserDTO(user));

                // create token
                String accessToken = this.securityUtil.createAccessToken(authentication.getName(), resLoginDTO);
                resLoginDTO.setAccessToken(accessToken);
                // config refresh token
                String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), resLoginDTO);

                // update refresh token to user
                this.userService.updateUserToken(refresh_token, loginDTO.getUsername());

                // set cookie to response(refresh token)
                ResponseCookie cookie = ResponseCookie.from("refresh_token", refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(resLoginDTO);
        }

        @GetMapping("/auth/account")
        @ApiMessage("Get account")
        public ResponseEntity<ResUserDTO> getAccount () throws IdInvalidException {
                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";
                User userCurrentDB = this.userService.handleGetUserByUsername(email);
                if(userCurrentDB == null){
                        throw new IdInvalidException("User not found");
                }
                ResUserDTO userGetAccount = this.userMapper.toResUserDTO(userCurrentDB);

                return ResponseEntity.ok(userGetAccount);
        }

        @GetMapping("/auth/refresh")
        @ApiMessage("Refresh token")
        public ResponseEntity<ResLoginDTO> getRefreshToken(
                        @CookieValue(name = "refresh_token", defaultValue = "") String refresh_token)
                        throws IdInvalidException {
                // check refresh token empty
                if (refresh_token.isEmpty()) {
                        throw new IdInvalidException("Refresh token is empty");
                }
                // check valid refresh token
                Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
                String email = decodedToken.getSubject();

                // check user by token and email
                User user = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
                if (user == null) {
                        throw new IdInvalidException("Refresh token is invalid");
                }

                ResLoginDTO resLoginDTO = new ResLoginDTO();

                resLoginDTO.setUser(this.userMapper.toResUserDTO(user));

                // create token
                String accessToken = this.securityUtil.createAccessToken(email, resLoginDTO);
                resLoginDTO.setAccessToken(accessToken);
                // config refresh token
                String new_refresh_token = this.securityUtil.createRefreshToken(email, resLoginDTO);

                // update refresh token to user
                this.userService.updateUserToken(new_refresh_token, email);

                // set cookie to response(refresh token)
                ResponseCookie cookie = ResponseCookie.from("refresh_token", new_refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(resLoginDTO);
        }

        @PostMapping("/auth/logout")
        @ApiMessage("Logout user")
        public ResponseEntity<Void> logout()
                        throws IdInvalidException {
                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";
                User user = this.userService.handleGetUserByUsername(email);
                if (user == null) {
                        throw new IdInvalidException("User not found");
                }
                user.setRefreshToken(null);
                this.userService.handleUpdateRefreshToken(user);
                ResponseCookie deleteCookieToken = ResponseCookie.from("refresh_token", null)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, deleteCookieToken.toString())
                                .build();
        }

        @PostMapping("/auth/register")
        public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User user)
                        throws IdInvalidException {
                boolean isEmailExist = this.userService.handleExistByEmail(user.getEmail());
                if (isEmailExist) {
                        throw new IdInvalidException("Email is exist");
                }
                user.setActive(false);
                //set role reader
                user.setRole(this.roleService.handleGetRoleById(2));
                User newUser = this.userService.handleCreateUser(user);
                //send email to user
                this.emailService.sendSimpleEmail(newUser.getEmail(), "Thông báo đăng ký thành công tài khoản thẻ của bạn đọc", "Trong thời gian 7 ngày, bạn đọc vui lòng đến thư viện để được cấp thẻ. Sau thời gian này đăng ký này sẽ không còn hiệu lực. Xin chân thành cảm ơn!");
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(this.userMapper.toResCreateUserDTO(newUser));
        }

}
