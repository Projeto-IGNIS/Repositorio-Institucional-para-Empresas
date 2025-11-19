package ignis.repositorio.institucional.user.repository;

import ignis.repositorio.institucional.user.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Group entity operations.
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    /**
     * Finds a group by name.
     */
    Optional<Group> findByName(String name);

    /**
     * Checks if a group exists by name.
     */
    boolean existsByName(String name);
}
