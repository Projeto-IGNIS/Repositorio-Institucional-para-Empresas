package ignis.repositorio.institucional.user.repository;

import ignis.repositorio.institucional.user.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Role entity operations.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a role by name.
     */
    Optional<Role> findByName(String name);

    /**
     * Checks if a role exists by name.
     */
    boolean existsByName(String name);
}
