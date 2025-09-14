package com.udacity.jwdnd.course1.cloudstorage.Repository;

import com.udacity.jwdnd.course1.cloudstorage.Entity.FileEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity,Long> {
    List<FileEntity> listForUser(Long userId);

    Optional<FileEntity> findByIdAndUsername(Long id, Long userId);

    @Modifying
    @Transactional
    @Query("delete from FileEntity f where f.id = :fileId and f.user.id = :userId")
    int deleteByIdAndOwner(@Param("fileId") Long fileId, @Param("userId") Long userId);
}
