package com.myhealth.repository;

import com.myhealth.entity.User;
import com.myhealth.projection.UserLoginProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    
    /**
     * Optimized query for login authentication that returns only required fields.
     * Uses JPQL with aggregation to collect roles as comma-separated string.
     * Avoids loading full User and UserProfile entities for better performance.
     */
    @Query("SELECT u.id as id, " +
           "u.username as username, " +
           "u.password as password, " +
           "u.enabled as enabled, " +
           "u.accountNonExpired as accountNonExpired, " +
           "u.accountNonLocked as accountNonLocked, " +
           "u.credentialsNonExpired as credentialsNonExpired, " +
           "COALESCE(STRING_AGG(r.name, ','), '') as roles " +
           "FROM User u " +
           "LEFT JOIN u.userRoles ur " +
           "LEFT JOIN ur.role r " +
           "WHERE u.username = :username " +
           "GROUP BY u.id, u.username, u.password, u.enabled, u.accountNonExpired, u.accountNonLocked, u.credentialsNonExpired")
    Optional<UserLoginProjection> findByUsernameForLogin(@Param("username") String username);
}