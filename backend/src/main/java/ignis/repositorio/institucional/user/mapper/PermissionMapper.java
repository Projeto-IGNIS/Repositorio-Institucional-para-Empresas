package ignis.repositorio.institucional.user.mapper;

import ignis.repositorio.institucional.user.domain.Permission;
import ignis.repositorio.institucional.user.dto.PermissionResponse;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper for Permission entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface PermissionMapper {

    /**
     * Maps Permission entity to PermissionResponse DTO.
     */
    PermissionResponse toResponse(Permission permission);

    /**
     * Maps set of Permission entities to set of PermissionResponse DTOs.
     */
    Set<PermissionResponse> toResponseSet(Set<Permission> permissions);

    /**
     * Maps list of Permission entities to list of PermissionResponse DTOs.
     */
    List<PermissionResponse> toResponseList(List<Permission> permissions);
}
