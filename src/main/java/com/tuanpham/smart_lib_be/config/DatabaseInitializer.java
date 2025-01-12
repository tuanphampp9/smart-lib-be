package com.tuanpham.smart_lib_be.config;

import com.tuanpham.smart_lib_be.domain.Permission;
import com.tuanpham.smart_lib_be.domain.Role;
import com.tuanpham.smart_lib_be.domain.User;
import com.tuanpham.smart_lib_be.repository.PermissionRepository;
import com.tuanpham.smart_lib_be.repository.RoleRepository;
import com.tuanpham.smart_lib_be.repository.UserRepository;
import com.tuanpham.smart_lib_be.util.constant.GenderEnum;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//execute code after the application is started
@Service
public class DatabaseInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(PermissionRepository permissionRepository, RoleRepository roleRepository,
                               UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>>> START INIT DATABASE");
        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();
        if (countPermissions == 0) {
            ArrayList<Permission> arrPermissions = new ArrayList<>();
//            arrPermissions.add(new Permission("Create a company", "/api/v1/companies", "POST", "COMPANIES"));
//            arrPermissions.add(new Permission("Update a company", "/api/v1/companies", "PUT", "COMPANIES"));
//            arrPermissions.add(new Permission("Delete a company", "/api/v1/companies/{id}", "DELETE", "COMPANIES"));
//            arrPermissions.add(new Permission("Get a company by id", "/api/v1/companies/{id}", "GET", "COMPANIES"));
//            arrPermissions
//                    .add(new Permission("Get companies with pagination", "/api/v1/companies", "GET", "COMPANIES"));
//
//            arrPermissions.add(new Permission("Create a job", "/api/v1/jobs", "POST", "JOBS"));
//            arrPermissions.add(new Permission("Update a job", "/api/v1/jobs", "PUT", "JOBS"));
//            arrPermissions.add(new Permission("Delete a job", "/api/v1/jobs/{id}", "DELETE", "JOBS"));
//            arrPermissions.add(new Permission("Get a job by id", "/api/v1/jobs/{id}", "GET", "JOBS"));
//            arrPermissions.add(new Permission("Get jobs with pagination", "/api/v1/jobs", "GET", "JOBS"));
//
//            arrPermissions.add(new Permission("Create a permission", "/api/v1/permissions", "POST", "PERMISSIONS"));
//            arrPermissions.add(new Permission("Update a permission", "/api/v1/permissions", "PUT", "PERMISSIONS"));
//            arrPermissions
//                    .add(new Permission("Delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
//            arrPermissions
//                    .add(new Permission("Get a permission by id", "/api/v1/permissions/{id}", "GET", "PERMISSIONS"));
//            arrPermissions.add(
//                    new Permission("Get permissions with pagination", "/api/v1/permissions", "GET", "PERMISSIONS"));
//
//            arrPermissions.add(new Permission("Create a resume", "/api/v1/resumes", "POST", "RESUMES"));
//            arrPermissions.add(new Permission("Update a resume", "/api/v1/resumes", "PUT", "RESUMES"));
//            arrPermissions.add(new Permission("Delete a resume", "/api/v1/resumes/{id}", "DELETE", "RESUMES"));
//            arrPermissions.add(new Permission("Get a resume by id", "/api/v1/resumes/{id}", "GET", "RESUMES"));
//            arrPermissions.add(new Permission("Get resumes with pagination", "/api/v1/resumes", "GET", "RESUMES"));
//
//            arrPermissions.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLES"));
//            arrPermissions.add(new Permission("Update a role", "/api/v1/roles", "PUT", "ROLES"));
//            arrPermissions.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE", "ROLES"));
//            arrPermissions.add(new Permission("Get a role by id", "/api/v1/roles/{id}", "GET", "ROLES"));
//            arrPermissions.add(new Permission("Get roles with pagination", "/api/v1/roles", "GET", "ROLES"));

            arrPermissions.add(new Permission("Create a user", "/api/v1/users", "POST", "USERS"));
            arrPermissions.add(new Permission("Update a user", "/api/v1/users", "PUT", "USERS"));
            arrPermissions.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS"));
            arrPermissions.add(new Permission("Get a user by id", "/api/v1/users/{id}", "GET", "USERS"));
            arrPermissions.add(new Permission("Get users with pagination", "/api/v1/users", "GET", "USERS"));

//            arrPermissions.add(new Permission("Create a subscriber", "/api/v1/subscribers", "POST", "SUBSCRIBERS"));
//            arrPermissions.add(new Permission("Update a subscriber", "/api/v1/subscribers", "PUT", "SUBSCRIBERS"));
//            arrPermissions
//                    .add(new Permission("Delete a subscriber", "/api/v1/subscribers/{id}", "DELETE", "SUBSCRIBERS"));
//            arrPermissions
//                    .add(new Permission("Get a subscriber by id", "/api/v1/subscribers/{id}", "GET", "SUBSCRIBERS"));
//            arrPermissions.add(
//                    new Permission("Get subscribers with pagination", "/api/v1/subscribers", "GET", "SUBSCRIBERS"));
//
//            arrPermissions.add(new Permission("Download a file", "/api/v1/files", "POST", "FILES"));
//            arrPermissions.add(new Permission("Upload a file", "/api/v1/files", "GET", "FILES"));
            this.permissionRepository.saveAll(arrPermissions);
        }

        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionRepository.findAll();
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Admin is full permissions");
            adminRole.setPermissions(allPermissions);
            this.roleRepository.save(adminRole);
        }
        if (countUsers == 0) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setAddress("hn");
            adminUser.setGender(GenderEnum.MALE);
            adminUser.setFullName("Thủ thư");
            adminUser.setPhone("0123456789");
            adminUser.setDob("1999-01-01");
            adminUser.setPortraitImg("http://res.cloudinary.com/dph5psbpr/image/upload/v1736180843/test/ril7mk6gqjnkezqmnoyb.jpg");
            adminUser.setPassword(this.passwordEncoder.encode("123456"));

            Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
            if (adminRole != null) {
                adminUser.setRole(adminRole);
            }

            this.userRepository.save(adminUser);
        }
        if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            System.out.println(">>>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        } else
            System.out.println(">>>> INIT DATABASE SUCCESSFULLY");
    }

}
