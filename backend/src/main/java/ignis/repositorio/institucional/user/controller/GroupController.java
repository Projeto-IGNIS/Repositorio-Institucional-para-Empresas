package ignis.repositorio.institucional.user.controller;

import ignis.repositorio.institucional.user.dto.CreateGroupRequest;
import ignis.repositorio.institucional.user.dto.GroupResponse;
import ignis.repositorio.institucional.user.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for group management.
 */
@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Tag(name = "Groups", description = "Group management endpoints")
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @Operation(summary = "Create a new group")
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        GroupResponse response = groupService.createGroup(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get group by ID")
    public ResponseEntity<GroupResponse> getGroupById(@PathVariable Long id) {
        GroupResponse response = groupService.getGroupById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all groups with pagination")
    public ResponseEntity<Page<GroupResponse>> getAllGroups(
            @Parameter(hidden = true) @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<GroupResponse> response = groupService.getAllGroups(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    @Operation(summary = "Get all groups as list")
    public ResponseEntity<List<GroupResponse>> getAllGroupsList() {
        List<GroupResponse> response = groupService.getAllGroupsList();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update group")
    public ResponseEntity<GroupResponse> updateGroup(
            @PathVariable Long id,
            @Valid @RequestBody CreateGroupRequest request) {
        GroupResponse response = groupService.updateGroup(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete group")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{groupId}/users/{userId}")
    @Operation(summary = "Add user to group")
    public ResponseEntity<GroupResponse> addUserToGroup(
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        GroupResponse response = groupService.addUserToGroup(groupId, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{groupId}/users/{userId}")
    @Operation(summary = "Remove user from group")
    public ResponseEntity<GroupResponse> removeUserFromGroup(
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        GroupResponse response = groupService.removeUserFromGroup(groupId, userId);
        return ResponseEntity.ok(response);
    }
}
