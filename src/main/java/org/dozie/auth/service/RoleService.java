package org.dozie.auth.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.dozie.auth.model.entity.Role;
import org.dozie.auth.model.exception.RoleServiceException;
import org.dozie.auth.repository.role.RoleRepository;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RoleService {
    @Inject
    RoleRepository roleRepository;

    @Transactional
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional
    public Role getRoleById(Long id) {
        Optional<Role> roleOpt = roleRepository.findById(id);
        if (roleOpt.isEmpty()) {
            throw new RoleServiceException(RoleServiceException.ErrorType.ROLE_NOT_FOUND, "Role with ID " + id + " not found");
        }
        return roleOpt.get();
    }

    @Transactional
    public Role getRoleByName(String name) {
        Optional<Role> roleOpt = roleRepository.findByName(name);
        return roleOpt.orElse(null);
    }

    @Transactional
    public Role createRole(Role role) {
        if (role == null || role.name == null || role.name.trim().isEmpty()) {
            throw new RoleServiceException(RoleServiceException.ErrorType.INVALID_NAME, "Role name cannot be empty");
        }
        if (roleRepository.findByName(role.name.trim()).isPresent()) {
            throw new RoleServiceException(RoleServiceException.ErrorType.DUPLICATE_NAME, "Role with this name already exists: " + role.name);
        }
        roleRepository.save(role);
        return role;
    }

    @Transactional
    public Role updateRole(Long id, Role role) {
        Optional<Role> entityOpt = roleRepository.findById(id);
        if (entityOpt.isEmpty()) {
            throw new RoleServiceException(RoleServiceException.ErrorType.ROLE_NOT_FOUND, "Role with ID " + id + " not found");
        }
        if (role == null || role.name == null || role.name.trim().isEmpty()) {
            throw new RoleServiceException(RoleServiceException.ErrorType.INVALID_NAME, "Role name cannot be empty");
        }
        Role entity = entityOpt.get();
        entity.name = role.name;
        entity.rolePermission = role.rolePermission;
        roleRepository.save(entity);
        return entity;
    }

    @Transactional
    public boolean deleteRole(Long id) {
        Optional<Role> entityOpt = roleRepository.findById(id);
        if (entityOpt.isEmpty()) {
            return false;
        }
        roleRepository.deleteById(id);
        return true;
    }
} 