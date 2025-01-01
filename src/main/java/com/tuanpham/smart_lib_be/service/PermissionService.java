package com.tuanpham.smart_lib_be.service;

import com.tuanpham.smart_lib_be.domain.Permission;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.repository.PermissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean handleExistsPermission(String module, String apiPath, String method) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(module, apiPath, method);
    }

    public Permission handleCreatePermission(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public Permission handleFindPermissionById(long id) {
        return this.permissionRepository.findById(id).orElse(null);
    }

    public Permission handleUpdatePermission(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public ResultPaginationDTO handleFindAllPermission(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermission = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);// current start from 1
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pagePermission.getTotalElements());// amount of elements
        meta.setPages(pagePermission.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(pagePermission.getContent());
        return resultPaginationDTO;
    }

    public void handleDeletePermission(long id) {
        // check if permission is used in role
        Optional<Permission> permission = this.permissionRepository.findById(id);
        Permission currentPermission = permission.get();
        // remove permission from role
        currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));
        // delete permission
        this.permissionRepository.deleteById(id);
    }

    public boolean isSameName(Permission permission) {
        Permission permissionFound = this.permissionRepository.findById(permission.getId()).orElse(null);
        if (permissionFound != null && permissionFound.getName().equals(permission.getName())) {
            return true;
        }
        return false;
    }
}
