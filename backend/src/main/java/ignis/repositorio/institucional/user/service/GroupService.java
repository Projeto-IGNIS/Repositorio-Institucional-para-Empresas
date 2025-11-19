package ignis.repositorio.institucional.user.service;

import ignis.repositorio.institucional.exception.ResourceAlreadyExistsException;
import ignis.repositorio.institucional.exception.ResourceNotFoundException;
import ignis.repositorio.institucional.user.domain.Group;
import ignis.repositorio.institucional.user.domain.User;
import ignis.repositorio.institucional.user.dto.CreateGroupRequest;
import ignis.repositorio.institucional.user.dto.GroupResponse;
import ignis.repositorio.institucional.user.mapper.GroupMapper;
import ignis.repositorio.institucional.user.repository.GroupRepository;
import ignis.repositorio.institucional.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing groups.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMapper groupMapper;

    /**
     * Creates a new group.
     */
    public GroupResponse createGroup(CreateGroupRequest request) {
        log.info("Creating new group with name: {}", request.getName());

        // Check if group already exists
        if (groupRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Group", "name", request.getName());
        }

        // Create group entity
        Group group = groupMapper.toEntity(request);
        Group savedGroup = groupRepository.save(group);

        log.info("Group created successfully with id: {}", savedGroup.getId());
        return groupMapper.toResponse(savedGroup);
    }

    /**
     * Gets a group by ID.
     */
    @Transactional(readOnly = true)
    public GroupResponse getGroupById(Long id) {
        log.debug("Fetching group with id: {}", id);
        
        Group group = groupRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Group", "id", id));
        
        return groupMapper.toResponse(group);
    }

    /**
     * Gets all groups with pagination.
     */
    @Transactional(readOnly = true)
    public Page<GroupResponse> getAllGroups(Pageable pageable) {
        log.debug("Fetching all groups with pagination");
        
        return groupRepository.findAll(pageable)
            .map(groupMapper::toResponse);
    }

    /**
     * Gets all groups as list.
     */
    @Transactional(readOnly = true)
    public List<GroupResponse> getAllGroupsList() {
        log.debug("Fetching all groups as list");
        
        return groupMapper.toResponseList(groupRepository.findAll());
    }

    /**
     * Updates a group.
     */
    public GroupResponse updateGroup(Long id, CreateGroupRequest request) {
        log.info("Updating group with id: {}", id);

        Group group = groupRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Group", "id", id));

        // Check if name is being changed and if it's already taken
        if (!request.getName().equals(group.getName()) &&
            groupRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Group", "name", request.getName());
        }

        group.setName(request.getName());
        group.setDescription(request.getDescription());

        Group updatedGroup = groupRepository.save(group);
        log.info("Group updated successfully with id: {}", updatedGroup.getId());

        return groupMapper.toResponse(updatedGroup);
    }

    /**
     * Deletes a group.
     */
    public void deleteGroup(Long id) {
        log.info("Deleting group with id: {}", id);

        if (!groupRepository.existsById(id)) {
            throw new ResourceNotFoundException("Group", "id", id);
        }

        groupRepository.deleteById(id);
        log.info("Group deleted successfully with id: {}", id);
    }

    /**
     * Adds a user to a group.
     */
    public GroupResponse addUserToGroup(Long groupId, Long userId) {
        log.info("Adding user {} to group {}", userId, groupId);

        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        group.getMembers().add(user);
        groupRepository.save(group);

        log.info("User added successfully to group");
        return groupMapper.toResponse(group);
    }

    /**
     * Removes a user from a group.
     */
    public GroupResponse removeUserFromGroup(Long groupId, Long userId) {
        log.info("Removing user {} from group {}", userId, groupId);

        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        group.getMembers().remove(user);
        groupRepository.save(group);

        log.info("User removed successfully from group");
        return groupMapper.toResponse(group);
    }
}
