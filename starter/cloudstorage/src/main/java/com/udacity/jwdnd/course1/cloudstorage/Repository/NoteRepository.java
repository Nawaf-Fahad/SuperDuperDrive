package com.udacity.jwdnd.course1.cloudstorage.Repository;

import com.udacity.jwdnd.course1.cloudstorage.Entity.FileEntity;
import com.udacity.jwdnd.course1.cloudstorage.Entity.NoteEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, Long> {

    List<NoteEntity> findByUser_IdOrderByIdDesc(Long userId);

    Optional<NoteEntity> findByIdAndUser_Id(Long id, Long userId);

    @Modifying @Transactional
    @Query("delete from NoteEntity n where n.id = :id and n.user.id = :userId")
    int deleteByIdAndOwner(@Param("id") Long id, @Param("userId") Long userId);
}
