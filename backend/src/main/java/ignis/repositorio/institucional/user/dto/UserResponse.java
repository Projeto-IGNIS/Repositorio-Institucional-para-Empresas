package ignis.repositorio.institucional.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for user response data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private Boolean active;
    private Set<RoleResponse> roles;
    private Set<GroupResponse> groups;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
