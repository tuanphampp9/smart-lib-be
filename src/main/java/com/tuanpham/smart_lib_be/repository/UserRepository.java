package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    User save(User user);

    void deleteById(String id);

    List<User> findAll();

    Optional<User> findById(String id);

    User findByEmail(String email);

    boolean existsByEmail(String email);

    User findByRefreshTokenAndEmail(String token, String email);

    @Query(value =
            """
            select * FROM users
            where users.role_id=2
            and users.active=1
            """, nativeQuery = true)
    List<User> findAllUsersActive();

}
