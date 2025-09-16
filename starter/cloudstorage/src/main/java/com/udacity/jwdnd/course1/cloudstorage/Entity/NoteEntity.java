package com.udacity.jwdnd.course1.cloudstorage.Entity;

import lombok.*;

import jakarta.persistence.*;
@Entity
@Table(name = "notes")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noteid")
    private Long id;

    @Column(name = "notetitle", length = 20, nullable = false)
    private String title;

    @Column(name = "notedescription", length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userid", nullable = false)
    private UserEntity user;
}
