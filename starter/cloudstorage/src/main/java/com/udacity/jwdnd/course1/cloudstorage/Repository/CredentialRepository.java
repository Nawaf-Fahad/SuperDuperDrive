package com.udacity.jwdnd.course1.cloudstorage.Repository;

import com.udacity.jwdnd.course1.cloudstorage.Entity.CredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<CredentialEntity, Long> {

    @Query("select c from CredentialEntity c where c.user.id = :userId order by c.id desc")
    List<CredentialEntity> listForUser(@Param("userId") Long userId);

    @Query("select c from CredentialEntity c where c.id = :id and c.user.id = :userId")
    Optional<CredentialEntity> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("delete from CredentialEntity c where c.id = :id and c.user.id = :userId")
    int deleteByIdAndOwner(@Param("id") Long id, @Param("userId") Long userId);
}
