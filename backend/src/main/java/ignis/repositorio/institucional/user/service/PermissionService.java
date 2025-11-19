package ignis.repositorio.institucional.user.service;

import ignis.repositorio.institucional.exception.ResourceNotFoundException;
import ignis.repositorio.institucional.user.domain.Permission;
import ignis.repositorio.institucional.user.dto.PermissionResponse;
import ignis.repositorio.institucional.user.mapper.PermissionMapper;
import ignis.repositorio.institucional.user.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing permissions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    /**
     * Gets a permission by ID.
     */
    public PermissionResponse getPermissionById(Long id) {
        log.debug("Fetching permission with id: {}", id);
        
        Permission permission = permissionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", id));
        
        return permissionMapper.toResponse(permission);
    }

    /**
     * Gets all permissions with pagination.
     */
    public Page<PermissionResponse> getAllPermissions(Pageable pageable) {
        log.debug("Fetching all permissions with pagination");
        
        return permissionRepository.findAll(pageable)
            .map(permissionMapper::toResponse);
    }

    /**
     * Gets all permissions as list.
     */
    public List<PermissionResponse> getAllPermissionsList() {
        log.debug("Fetching all permissions as list");
        
        return permissionMapper.toResponseList(permissionRepository.findAll());
    }

    /**
     * Gets permissions by resource.
     */
    public List<PermissionResponse> getPermissionsByResource(String resource) {
        log.debug("Fetching permissions for resource: {}", resource);
        
        List<Permission> permissions = permissionRepository.findAll().stream()
            .filter(p -> p.getResource().equalsIgnoreCase(resource))
            .toList();
        
        return permissionMapper.toResponseList(permissions);
    }
}
