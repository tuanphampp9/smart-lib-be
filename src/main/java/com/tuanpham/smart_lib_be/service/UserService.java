package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.*;
import com.tuanpham.smart_lib_be.domain.Request.CartUserReq;
import com.tuanpham.smart_lib_be.domain.Request.PubRatingReq;
import com.tuanpham.smart_lib_be.domain.Request.ReqForgetPassword;
import com.tuanpham.smart_lib_be.domain.Response.*;
import com.tuanpham.smart_lib_be.mapper.BorrowSlipMapper;
import com.tuanpham.smart_lib_be.mapper.UserMapper;
import com.tuanpham.smart_lib_be.repository.*;
import com.tuanpham.smart_lib_be.util.SecurityUtil;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final PublicationRatingRepository publicationRatingRepository;
    private final PublicationRepository publicationRepository;
    private final CartUserRepository cartUserRepository;
    private final BorrowSlipDetailRepository borrowSlipDetailRepository;
    private final BorrowSlipMapper borrowSlipMapper;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper, PublicationRepository publicationRepository,
                       RoleService roleService, PublicationRatingRepository publicationRatingRepository,
                       CartUserRepository cartUserRepository, BorrowSlipDetailRepository borrowSlipDetailRepository,
                       BorrowSlipMapper borrowSlipMapper, EmailService emailService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.userMapper = userMapper;
        this.publicationRatingRepository = publicationRatingRepository;
        this.publicationRepository = publicationRepository;
        this.cartUserRepository = cartUserRepository;
        this.borrowSlipDetailRepository = borrowSlipDetailRepository;
        this.borrowSlipMapper = borrowSlipMapper;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public User handleCreateUser(User user) {
        // check role
        if (user.getRole() != null) {
            Role role = this.roleService.handleGetRoleById(user.getRole().getId());
            user.setRole(role);
        }
        return this.userRepository.save(user);
    }

    public void handleDeleteUser(String id) {
        this.userRepository.deleteById(id);
    }

    public User handleGetUser(String id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();
        resCreateUserDTO.setId(user.getId());
        resCreateUserDTO.setFullName(user.getFullName());
        resCreateUserDTO.setEmail(user.getEmail());
        resCreateUserDTO.setGender(user.getGender());
        resCreateUserDTO.setAddress(user.getAddress());
        resCreateUserDTO.setCreatedAt(user.getCreatedAt());
        resCreateUserDTO.setPortraitImg(user.getPortraitImg());
        return resCreateUserDTO;
    }

    public User handleUpdateUser(User newUser, User userFound) {
        userFound.setAddress(newUser.getAddress());
        userFound.setEmail(newUser.getEmail());
        userFound.setGender(newUser.getGender());
        userFound.setFullName(newUser.getFullName());
        userFound.setPortraitImg(newUser.getPortraitImg());
        userFound.setDob(newUser.getDob());
        // check role
        if (newUser.getRole() != null) {
            Role role = this.roleService.handleGetRoleById(newUser.getRole().getId());
            userFound.setRole(role);
        }
        return this.userRepository.save(userFound);
    }

    public User handleUpdateRefreshToken(User user) {
        return this.userRepository.save(user);
    }

    public ResultPaginationDTO handleGetAllUser(
            Specification<User> spec,
            Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageUser.getSize());
        meta.setTotal(pageUser.getTotalElements());// amount of elements
        meta.setPages(pageUser.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<ResUserDTO> listUser = pageUser.getContent().stream().map(
                u -> this.userMapper.toResUserDTO(u))
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listUser);
        return resultPaginationDTO;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public boolean handleCheckUserExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.role role = new ResUserDTO.role();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setFullName(user.getFullName());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setPortraitImg(user.getPortraitImg());
        res.setDob(user.getDob());

        if (user.getRole() != null) {
            role.setId(user.getRole().getId());
            role.setName(user.getRole().getName());
            res.setRole(role);
        }
        return res;
    }

    public ResUpdateDTO convertToResUpdateDTO(User user) {
        ResUpdateDTO res = new ResUpdateDTO();
        res.setId(user.getId());
        res.setFullName(user.getFullName());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setPortraitImg(user.getPortraitImg());

        return res;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.handleUpdateRefreshToken(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    public boolean handleExistById(String id) {
        return this.userRepository.existsById(id);
    }

    public boolean handleExistByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User handleGetUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public BorrowSlipRes handleCreateRating(PubRatingReq pubRatingReq) throws IdInvalidException {
        Publication publication = this.publicationRepository.findById(pubRatingReq.getPublicationId()).orElse(null);
        User user = this.userRepository.findById(pubRatingReq.getUserId()).orElse(null);
        BorrowSlipDetail borrowSlipDetail = this.borrowSlipDetailRepository.findById(pubRatingReq.getBorrowSlipDetailId()).orElse(null);
//        PublicationRating publicationRatingExist = this.publicationRatingRepository.findByUserIdAndPublicationId(
//                pubRatingReq.getUserId(), pubRatingReq.getPublicationId());
        if (user == null) {
            throw new IdInvalidException("Người dùng không tồn tại");
        }
        if (publication == null) {
            throw new IdInvalidException("Ấn phẩm không tồn tại");
        }
//        if(publicationRatingExist != null) {
//            publicationRatingExist.setRating(pubRatingReq.getRating());
//            this.publicationRatingRepository.save(publicationRatingExist);
//            return;
//        }
        PublicationRating publicationRating = new PublicationRating();
        publicationRating.setPublicationId(pubRatingReq.getPublicationId());
        publicationRating.setUserId(pubRatingReq.getUserId());
        publicationRating.setRating(pubRatingReq.getRating());
        this.publicationRatingRepository.save(publicationRating);
        if(borrowSlipDetail != null) {
            borrowSlipDetail.setPublicationRating(publicationRating);
            this.borrowSlipDetailRepository.save(borrowSlipDetail);
        }
        BorrowSlipRes borrowSlipRes = this.borrowSlipMapper.toBorrowSlipRes(borrowSlipDetail.getBorrowSlip());
        List<BorrowSlipRes.BorrowSlipDetailRes> borrowSlipDetailResList = borrowSlipDetail.getBorrowSlip().getBorrowSlipDetails().stream().map(
                bd -> {
                    BorrowSlipRes.BorrowSlipDetailRes borrowSlipDetailRes = new BorrowSlipRes.BorrowSlipDetailRes();
                    borrowSlipDetailRes.setId(bd.getId());
                    borrowSlipDetailRes.setNameBook(bd.getRegistrationUnique().getImportReceiptDetail().getPublication().getName());
                    borrowSlipDetailRes.setPublicationId(bd.getRegistrationUnique().getImportReceiptDetail().getPublication().getId());
                    borrowSlipDetailRes.setRegistrationUnique(bd.getRegistrationUnique());
                    borrowSlipDetailRes.setPublicationRating(bd.getPublicationRating());
                    return borrowSlipDetailRes;
                }
        ).collect(Collectors.toList());
        borrowSlipRes.setBorrowSlipDetails(borrowSlipDetailResList);
        return borrowSlipRes;
    }

    public Integer handleGetUserRatingByPublicationId(String userId, Long publicationId) {
        PublicationRating publicationRating = this.publicationRatingRepository.findByUserIdAndPublicationId(userId, publicationId);
        if (publicationRating == null) {
            return 0;
        }
        return publicationRating.getRating();
    }

    public CartUser handleAddPublicationToCart(CartUserReq cartUserReq) throws IdInvalidException {
        CartUser cartUserExist = this.cartUserRepository.findByUserIdAndPublicationId(cartUserReq.getUserId(), cartUserReq.getPublicationId());
        Publication publicationExist = this.publicationRepository.findById(cartUserReq.getPublicationId()).orElse(null);
        Long totalQuantityPubCanBorrow = this.cartUserRepository.countQuantityCanBorrow(cartUserReq.getPublicationId());
        if (publicationExist == null) {
            throw new IdInvalidException("Không tìm thấy ấn phẩm");
        }
        User userExist = this.userRepository.findById(cartUserReq.getUserId()).orElse(null);
        if (userExist == null) {
            throw new IdInvalidException("Không tìm thấy người dùng");
        }
        if (cartUserExist != null) {
            int quantity = cartUserExist.getQuantity() + cartUserReq.getQuantity();
            if(totalQuantityPubCanBorrow < quantity) {
                throw new IdInvalidException("Số lượng trong kho không đủ");
            }
            cartUserExist.setQuantity(quantity);
            return this.cartUserRepository.save(cartUserExist);
        }
        if(totalQuantityPubCanBorrow < cartUserReq.getQuantity()) {
            throw new IdInvalidException("Số lượng trong kho không đủ");
        }
        CartUser cartUser = new CartUser();
        cartUser.setPublicationId(cartUserReq.getPublicationId());
        cartUser.setUserId(cartUserReq.getUserId());
        cartUser.setQuantity(cartUserReq.getQuantity());
        cartUser.setPublication(publicationExist);
        cartUser.setUser(userExist);
        return this.cartUserRepository.save(cartUser);
    }
    public void handleRemovePublicationFromCart(String id) throws IdInvalidException {
        CartUser cartUserExist = this.cartUserRepository.findById(id).orElse(null);
        if (cartUserExist == null) {
            throw new IdInvalidException("Không tìm thấy giỏ hàng");
        }
        this.cartUserRepository.deleteById(id);
    }

    public void handleMinusPublicationFromCart(String id) throws IdInvalidException {
        CartUser cartUserExist = this.cartUserRepository.findById(id).orElse(null);
        if (cartUserExist == null) {
            throw new IdInvalidException("Không tìm thấy giỏ hàng");
        }
        int quantity = cartUserExist.getQuantity() - 1;
        if (quantity == 0) {
            throw new IdInvalidException("Số lượng không thể nhỏ hơn 1");
        }
        cartUserExist.setQuantity(quantity);
        this.cartUserRepository.save(cartUserExist);
    }

    public User handleForgetPassword(ReqForgetPassword reqForgetPassword) throws IdInvalidException {
        User user = this.userRepository.findByEmail(reqForgetPassword.getEmail());
        if (user == null) {
            throw new IdInvalidException("Email không tồn tại");
        }
        String newPassword = SecurityUtil.generateSecurePassword();
        //send email to user
        this.emailService.sendSimpleEmail(user.getEmail(), "Thông báo mật khẩu mới", "Mật khẩu mới của bạn là: " + newPassword+" . Vui lòng đăng nhập và đổi mật khẩu ngay sau khi đăng nhập");
        //create password
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        return this.userRepository.save(user);
    }

    //get list userid
    public List<User> getListUsersActive() {
        return this.userRepository.findAllUsersActive();
    }

    //if after 7 day, user not active account, account will be deleted
    //cron run every saturday (8 AM)
    @Scheduled(cron = "0 0 8 ? * SAT")
    public void deleteUserNotActive() {
        List<User> users = this.userRepository.findAll();
        for (User user : users) {
            if (
                    user.getCreatedAt().plus(7, java.time.temporal.ChronoUnit.DAYS).isBefore(Instant.now())
                            && !user.isActive()
            ) {
                this.userRepository.delete(user);
            }
        }
    }
}
