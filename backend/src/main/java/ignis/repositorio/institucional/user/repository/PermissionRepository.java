package ignis.repositorio.institucional.user.repository;

import ignis.repositorio.institucional.user.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Permission entity operations.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Finds a permission by name.
     */
    Optional<Permission> findByName(String name);

    /**
     * Finds a permission by resource and action.
     */
    Optional<Permission> findByResourceAndAction(String resource, String action);

    /**
     * Checks if a permission exists by name.
     */
    boolean existsByName(String name);
}
