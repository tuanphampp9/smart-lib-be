package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.Permission;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.domain.Role;
import com.tuanpham.smart_lib_be.repository.PermissionRepository;
import com.tuanpham.smart_lib_be.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean handleExistsRole(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role handleCreateRole(Role role) {
        // check permissions
        if (role.getPermissions() != null) {
            List<Long> listPermissionIds = role.getPermissions().stream().map(permission -> permission.getId())
                    .collect(Collectors.toList());
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(listPermissionIds);
            role.setPermissions(dbPermissions);
        }
        return this.roleRepository.save(role);
    }

    public Role handleFindRoleById(Long id) {
        return this.roleRepository.findById(id).orElse(null);
    }

    public Role handleUpdateRole(Role newRole, Role roleFound) {
        roleFound.setName(newRole.getName());
        roleFound.setDescription(newRole.getDescription());
        roleFound.setActive(newRole.isActive());
        // check permissions
        if (newRole.getPermissions() != null) {
            List<Long> listPermissionIds = newRole.getPermissions().stream().map(permission -> permission.getId())
                    .collect(Collectors.toList());
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(listPermissionIds);
            roleFound.setPermissions(dbPermissions);
        }
        return this.roleRepository.save(roleFound);
    }

    public ResultPaginationDTO handleFindAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);// current start from 1
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pageRole.getTotalElements());// amount of elements
        meta.setPages(pageRole.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(pageRole.getContent());
        return resultPaginationDTO;
    }

    public void handleDeleteRole(Long id) {
        this.roleRepository.deleteById(id);
    }

    public Role handleGetRoleById(long id) {
        return this.roleRepository.findById(id).orElse(null);
    }
}
