package ignis.repositorio.institucional.user.service;

import ignis.repositorio.institucional.exception.ResourceAlreadyExistsException;
import ignis.repositorio.institucional.exception.ResourceNotFoundException;
import ignis.repositorio.institucional.user.domain.Group;
import ignis.repositorio.institucional.user.domain.Role;
import ignis.repositorio.institucional.user.domain.User;
import ignis.repositorio.institucional.user.dto.CreateUserRequest;
import ignis.repositorio.institucional.user.dto.UpdateUserRequest;
import ignis.repositorio.institucional.user.dto.UserResponse;
import ignis.repositorio.institucional.user.mapper.UserMapper;
import ignis.repositorio.institucional.user.repository.GroupRepository;
import ignis.repositorio.institucional.user.repository.RoleRepository;
import ignis.repositorio.institucional.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service for managing users.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GroupRepository groupRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user.
     */
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating new user with username: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("User", "username", request.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("User", "email", request.getEmail());
        }

        // Create user entity
        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Assign roles
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (Long roleId : request.getRoleIds()) {
                Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        // Assign groups
        if (request.getGroupIds() != null && !request.getGroupIds().isEmpty()) {
            Set<Group> groups = new HashSet<>();
            for (Long groupId : request.getGroupIds()) {
                Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
                groups.add(group);
            }
            user.setGroups(groups);
        }

        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    /**
     * Gets a user by ID.
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Fetching user with id: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        return userMapper.toResponse(user);
    }

    /**
     * Gets a user by username.
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        log.debug("Fetching user with username: {}", username);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return userMapper.toResponse(user);
    }

    /**
     * Gets all users with pagination.
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.debug("Fetching all users with pagination");
        
        return userRepository.findAll(pageable)
            .map(userMapper::toResponse);
    }

    /**
     * Updates a user.
     */
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        log.info("Updating user with id: {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Check if username is being changed and if it's already taken
        if (request.getUsername() != null && 
            !request.getUsername().equals(user.getUsername()) &&
            userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("User", "username", request.getUsername());
        }

        // Check if email is being changed and if it's already taken
        if (request.getEmail() != null && 
            !request.getEmail().equals(user.getEmail()) &&
            userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("User", "email", request.getEmail());
        }

        // Update basic fields
        userMapper.updateEntity(request, user);

        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        // Update roles if provided
        if (request.getRoleIds() != null) {
            Set<Role> roles = new HashSet<>();
            for (Long roleId : request.getRoleIds()) {
                Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        // Update groups if provided
        if (request.getGroupIds() != null) {
            Set<Group> groups = new HashSet<>();
            for (Long groupId : request.getGroupIds()) {
                Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
                groups.add(group);
            }
            user.setGroups(groups);
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with id: {}", updatedUser.getId());

        return userMapper.toResponse(updatedUser);
    }

    /**
     * Deletes a user.
     */
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }

    /**
     * Activates a user.
     */
    public UserResponse activateUser(Long id) {
        log.info("Activating user with id: {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setActive(true);
        User updatedUser = userRepository.save(user);

        log.info("User activated successfully with id: {}", id);
        return userMapper.toResponse(updatedUser);
    }

    /**
     * Deactivates a user.
     */
    public UserResponse deactivateUser(Long id) {
        log.info("Deactivating user with id: {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setActive(false);
        User updatedUser = userRepository.save(user);

        log.info("User deactivated successfully with id: {}", id);
        return userMapper.toResponse(updatedUser);
    }
}
