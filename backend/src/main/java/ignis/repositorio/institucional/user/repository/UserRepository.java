package ignis.repositorio.institucional.user.repository;

import ignis.repositorio.institucional.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists by username.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists by email.
     */
    boolean existsByEmail(String email);

    /**
     * Finds a user by username or email.
     */
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);
}
