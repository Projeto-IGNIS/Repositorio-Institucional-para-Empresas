package ignis.repositorio.institucional.user.controller;

import ignis.repositorio.institucional.user.dto.PermissionResponse;
import ignis.repositorio.institucional.user.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for permission management.
 */
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions", description = "Permission management endpoints")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/{id}")
    @Operation(summary = "Get permission by ID")
    public ResponseEntity<PermissionResponse> getPermissionById(@PathVariable Long id) {
        PermissionResponse response = permissionService.getPermissionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all permissions with pagination")
    public ResponseEntity<Page<PermissionResponse>> getAllPermissions(
            @Parameter(hidden = true) @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<PermissionResponse> response = permissionService.getAllPermissions(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    @Operation(summary = "Get all permissions as list")
    public ResponseEntity<List<PermissionResponse>> getAllPermissionsList() {
        List<PermissionResponse> response = permissionService.getAllPermissionsList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/resource/{resource}")
    @Operation(summary = "Get permissions by resource")
    public ResponseEntity<List<PermissionResponse>> getPermissionsByResource(@PathVariable String resource) {
        List<PermissionResponse> response = permissionService.getPermissionsByResource(resource);
        return ResponseEntity.ok(response);
    }
}
