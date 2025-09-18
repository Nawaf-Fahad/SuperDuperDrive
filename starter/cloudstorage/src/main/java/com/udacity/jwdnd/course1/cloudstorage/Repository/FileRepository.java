package com.udacity.jwdnd.course1.cloudstorage.Repository;

import com.udacity.jwdnd.course1.cloudstorage.Entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity,Long> {
    @Query("select f from FileEntity f where f.user.id = :userId")
    List<FileEntity> listForUser(@Param("userId") Long userId);

    @Query("select f from FileEntity f where f.id = :id and f.user.id = :userId")
    Optional<FileEntity> findByIdAndUsername(@Param("id") Long id, @Param("userId") Long userId);

    // Check for duplicate filenames (case-insensitive) for a given user
    Optional<FileEntity> findByUser_IdAndFilenameIgnoreCase(Long userId, String filename);

    @Modifying
    @Transactional
    @Query("delete from FileEntity f where f.id = :fileId and f.user.id = :userId")
    int deleteByIdAndOwner(@Param("fileId") Long fileId, @Param("userId") Long userId);
}
