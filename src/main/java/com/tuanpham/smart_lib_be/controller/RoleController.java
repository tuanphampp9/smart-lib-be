package com.tuanpham.smart_lib_be.controller;

import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.domain.Role;
import com.tuanpham.smart_lib_be.service.RoleService;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    public ResponseEntity<Role> create(@Valid @RequestBody Role role)
            throws IdInvalidException {
        boolean isExist = this.roleService.handleExistsRole(role.getName());
        if (isExist) {
            throw new IdInvalidException("Role already exists");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.handleCreateRole(role));
    }

    @PutMapping("roles")
    public ResponseEntity<Role> update(@Valid @RequestBody Role role)
            throws IdInvalidException {
        Role roleFound = this.roleService.handleFindRoleById(role.getId());
        if (roleFound == null) {
            throw new IdInvalidException("Role not found");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.handleUpdateRole(role, roleFound));
    }

    @GetMapping("/roles")
    public ResponseEntity<ResultPaginationDTO> getAllRoles(
            @Filter Specification<Role> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.roleService.handleFindAllRoles(spec, pageable));
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") long id)
            throws IdInvalidException {
        Role role = this.roleService.handleFindRoleById(id);
        if (role == null) {
            throw new IdInvalidException("Role not found");
        }
        return ResponseEntity.ok().body(role);
    }

    @DeleteMapping("roles/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id)
            throws IdInvalidException {
        Role role = this.roleService.handleFindRoleById(id);
        if (role == null) {
            throw new IdInvalidException("Role not found");
        }
        this.roleService.handleDeleteRole(id);
        return ResponseEntity.ok().body(null);
    }

}
