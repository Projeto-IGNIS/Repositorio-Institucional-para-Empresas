package ignis.repositorio.institucional.user.service;

import ignis.repositorio.institucional.exception.ResourceAlreadyExistsException;
import ignis.repositorio.institucional.exception.ResourceNotFoundException;
import ignis.repositorio.institucional.user.domain.Permission;
import ignis.repositorio.institucional.user.domain.Role;
import ignis.repositorio.institucional.user.dto.CreateRoleRequest;
import ignis.repositorio.institucional.user.dto.RoleResponse;
import ignis.repositorio.institucional.user.mapper.RoleMapper;
import ignis.repositorio.institucional.user.repository.PermissionRepository;
import ignis.repositorio.institucional.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service for managing roles.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    /**
     * Creates a new role.
     */
    public RoleResponse createRole(CreateRoleRequest request) {
        log.info("Creating new role with name: {}", request.getName());

        // Check if role already exists
        if (roleRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Role", "name", request.getName());
        }

        // Create role entity
        Role role = roleMapper.toEntity(request);

        // Assign permissions
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = new HashSet<>();
            for (Long permissionId : request.getPermissionIds()) {
                Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", permissionId));
                permissions.add(permission);
            }
            role.setPermissions(permissions);
        }

        Role savedRole = roleRepository.save(role);
        log.info("Role created successfully with id: {}", savedRole.getId());

        return roleMapper.toResponse(savedRole);
    }

    /**
     * Gets a role by ID.
     */
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        log.debug("Fetching role with id: {}", id);
        
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        
        return roleMapper.toResponse(role);
    }

    /**
     * Gets all roles with pagination.
     */
    @Transactional(readOnly = true)
    public Page<RoleResponse> getAllRoles(Pageable pageable) {
        log.debug("Fetching all roles with pagination");
        
        return roleRepository.findAll(pageable)
            .map(roleMapper::toResponse);
    }

    /**
     * Gets all roles as list.
     */
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRolesList() {
        log.debug("Fetching all roles as list");
        
        return roleMapper.toResponseList(roleRepository.findAll());
    }

    /**
     * Updates a role.
     */
    public RoleResponse updateRole(Long id, CreateRoleRequest request) {
        log.info("Updating role with id: {}", id);

        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));

        // Check if name is being changed and if it's already taken
        if (!request.getName().equals(role.getName()) &&
            roleRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Role", "name", request.getName());
        }

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        // Update permissions
        if (request.getPermissionIds() != null) {
            Set<Permission> permissions = new HashSet<>();
            for (Long permissionId : request.getPermissionIds()) {
                Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", permissionId));
                permissions.add(permission);
            }
            role.setPermissions(permissions);
        }

        Role updatedRole = roleRepository.save(role);
        log.info("Role updated successfully with id: {}", updatedRole.getId());

        return roleMapper.toResponse(updatedRole);
    }

    /**
     * Deletes a role.
     */
    public void deleteRole(Long id) {
        log.info("Deleting role with id: {}", id);

        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", "id", id);
        }

        roleRepository.deleteById(id);
        log.info("Role deleted successfully with id: {}", id);
    }

    /**
     * Adds a permission to a role.
     */
    public RoleResponse addPermissionToRole(Long roleId, Long permissionId) {
        log.info("Adding permission {} to role {}", permissionId, roleId);

        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));

        Permission permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", permissionId));

        role.getPermissions().add(permission);
        Role updatedRole = roleRepository.save(role);

        log.info("Permission added successfully to role");
        return roleMapper.toResponse(updatedRole);
    }

    /**
     * Removes a permission from a role.
     */
    public RoleResponse removePermissionFromRole(Long roleId, Long permissionId) {
        log.info("Removing permission {} from role {}", permissionId, roleId);

        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));

        Permission permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", permissionId));

        role.getPermissions().remove(permission);
        Role updatedRole = roleRepository.save(role);

        log.info("Permission removed successfully from role");
        return roleMapper.toResponse(updatedRole);
    }
}
