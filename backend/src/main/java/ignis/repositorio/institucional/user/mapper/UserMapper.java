package ignis.repositorio.institucional.user.mapper;

import ignis.repositorio.institucional.user.domain.User;
import ignis.repositorio.institucional.user.dto.CreateUserRequest;
import ignis.repositorio.institucional.user.dto.UpdateUserRequest;
import ignis.repositorio.institucional.user.dto.UserResponse;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for User entity and DTOs.
 */
@Mapper(componentModel = "spring", uses = {RoleMapper.class, GroupMapper.class})
public interface UserMapper {

    /**
     * Maps User entity to UserResponse DTO.
     */
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "groups", source = "groups")
    UserResponse toResponse(User user);

    /**
     * Maps list of User entities to list of UserResponse DTOs.
     */
    List<UserResponse> toResponseList(List<User> users);

    /**
     * Maps CreateUserRequest DTO to User entity.
     * Password hashing should be done in the service layer.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(CreateUserRequest request);

    /**
     * Updates an existing User entity with data from UpdateUserRequest.
     * Null values in the request are ignored.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateUserRequest request, @MappingTarget User user);
}
