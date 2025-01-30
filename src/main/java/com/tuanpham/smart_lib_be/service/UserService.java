package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.Publication;
import com.tuanpham.smart_lib_be.domain.PublicationRating;
import com.tuanpham.smart_lib_be.domain.Request.PubRatingReq;
import com.tuanpham.smart_lib_be.domain.Response.ResCreateUserDTO;
import com.tuanpham.smart_lib_be.domain.Response.ResUpdateDTO;
import com.tuanpham.smart_lib_be.domain.Response.ResUserDTO;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.domain.Role;
import com.tuanpham.smart_lib_be.domain.User;
import com.tuanpham.smart_lib_be.mapper.UserMapper;
import com.tuanpham.smart_lib_be.repository.PublicationRatingRepository;
import com.tuanpham.smart_lib_be.repository.PublicationRepository;
import com.tuanpham.smart_lib_be.repository.PublisherRepository;
import com.tuanpham.smart_lib_be.repository.UserRepository;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


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

    public UserService(UserRepository userRepository,
                       UserMapper userMapper, PublicationRepository publicationRepository,
                       RoleService roleService, PublicationRatingRepository publicationRatingRepository) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.userMapper = userMapper;
        this.publicationRatingRepository = publicationRatingRepository;
        this.publicationRepository = publicationRepository;
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

    public void handleCreateRating(PubRatingReq pubRatingReq) throws IdInvalidException {
        Publication publication = this.publicationRepository.findById(pubRatingReq.getPublicationId()).orElse(null);
        User user = this.userRepository.findById(pubRatingReq.getUserId()).orElse(null);
        PublicationRating publicationRatingExist = this.publicationRatingRepository.findByUserIdAndPublicationId(
                pubRatingReq.getUserId(), pubRatingReq.getPublicationId());
        if (user == null) {
            throw new IdInvalidException("Người dùng không tồn tại");
        }
        if (publication == null) {
            throw new IdInvalidException("Ấn phẩm không tồn tại");
        }
        if(publicationRatingExist != null) {
            publicationRatingExist.setRating(pubRatingReq.getRating());
            this.publicationRatingRepository.save(publicationRatingExist);
            return;
        }
        PublicationRating publicationRating = new PublicationRating();
        publicationRating.setPublicationId(pubRatingReq.getPublicationId());
        publicationRating.setUserId(pubRatingReq.getUserId());
        publicationRating.setRating(pubRatingReq.getRating());
        this.publicationRatingRepository.save(publicationRating);
    }

    public Integer handleGetUserRatingByPublicationId(String userId, Long publicationId) {
        PublicationRating publicationRating = this.publicationRatingRepository.findByUserIdAndPublicationId(userId, publicationId);
        if (publicationRating == null) {
            return 0;
        }
        return publicationRating.getRating();
    }
}
