package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.RoleUser;
import com.example.managementadmissionwf.dal.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Integer> {

    // Standard queries (auto-filtered by @SQLRestriction)

    Optional<Users> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<Users> findByRole(RoleUser role, Pageable pageable);

    Page<Users> findByFullnameContainingOrUsernameContainingOrEmailContaining(
            String kw1, String kw2, String kw3, Pageable pageable);

    @Query("SELECT u FROM Users u WHERE u.role = :role AND " +
            "(LOWER(u.fullname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Users> findByRoleAndKeyword(@Param("role") RoleUser role, @Param("keyword") String keyword, Pageable pageable);

    // Queries for excluding current user from list
    Page<Users> findAllByIdNot(Integer id, Pageable pageable);

    Page<Users> findAllByIdNotAndRole(Integer id, RoleUser role, Pageable pageable);

    @Query("SELECT u FROM Users u WHERE u.id <> :excludeId AND " +
            "(LOWER(u.fullname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Users> findByKeywordExcludingId(@Param("keyword") String keyword, @Param("excludeId") Integer excludeId, Pageable pageable);

    @Query("SELECT u FROM Users u WHERE u.id <> :excludeId AND u.role = :role AND " +
            "(LOWER(u.fullname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Users> findByRoleAndKeywordExcludingId(@Param("role") RoleUser role, @Param("keyword") String keyword, @Param("excludeId") Integer excludeId, Pageable pageable);

    // Raw SQL queries that bypass @SQLRestriction for soft delete operations

    @Modifying
    @Query(value = "UPDATE users SET is_deleted = true WHERE id = :id", nativeQuery = true)
    void softDeleteById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM users WHERE id = :id", nativeQuery = true)
    Optional<Users> findByIdRaw(@Param("id") Integer id);

    boolean existsByIdAndIsDeletedFalse(Integer id);
}
