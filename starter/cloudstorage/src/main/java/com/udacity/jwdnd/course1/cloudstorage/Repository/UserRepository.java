package com.udacity.jwdnd.course1.cloudstorage.Repository;

import java.util.Optional;

import com.udacity.jwdnd.course1.cloudstorage.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUsername(String username);
    Optional<UserEntity> findByUsername(String username);
}