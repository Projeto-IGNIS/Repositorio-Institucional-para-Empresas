package ignis.repositorio.institucional.user.mapper;

import ignis.repositorio.institucional.user.domain.Group;
import ignis.repositorio.institucional.user.dto.CreateGroupRequest;
import ignis.repositorio.institucional.user.dto.GroupResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper for Group entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface GroupMapper {

    /**
     * Maps Group entity to GroupResponse DTO.
     */
    GroupResponse toResponse(Group group);

    /**
     * Maps set of Group entities to set of GroupResponse DTOs.
     */
    Set<GroupResponse> toResponseSet(Set<Group> groups);

    /**
     * Maps list of Group entities to list of GroupResponse DTOs.
     */
    List<GroupResponse> toResponseList(List<Group> groups);

    /**
     * Maps CreateGroupRequest DTO to Group entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Group toEntity(CreateGroupRequest request);
}
