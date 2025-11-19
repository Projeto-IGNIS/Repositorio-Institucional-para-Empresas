package ignis.repositorio.institucional.user.mapper;

import ignis.repositorio.institucional.user.domain.Role;
import ignis.repositorio.institucional.user.dto.CreateRoleRequest;
import ignis.repositorio.institucional.user.dto.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper for Role entity and DTOs.
 */
@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface RoleMapper {

    /**
     * Maps Role entity to RoleResponse DTO.
     */
    @Mapping(target = "permissions", source = "permissions")
    RoleResponse toResponse(Role role);

    /**
     * Maps set of Role entities to set of RoleResponse DTOs.
     */
    Set<RoleResponse> toResponseSet(Set<Role> roles);

    /**
     * Maps list of Role entities to list of RoleResponse DTOs.
     */
    List<RoleResponse> toResponseList(List<Role> roles);

    /**
     * Maps CreateRoleRequest DTO to Role entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Role toEntity(CreateRoleRequest request);
}
