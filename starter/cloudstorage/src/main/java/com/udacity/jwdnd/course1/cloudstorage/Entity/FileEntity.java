package com.udacity.jwdnd.course1.cloudstorage.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "files")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fileid") private Long id;

    @Column(name = "filename", nullable = false)     private String filename;
    @Column(name = "contenttype", nullable = false)  private String contentType;
    @Column(name = "filesize", nullable = false)     private String fileSize;
    @Lob @Basic(fetch = FetchType.LAZY)
    @Column(name = "filedata", nullable = false)     private byte[] fileData;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userid", nullable = false)   private UserEntity user;
}
