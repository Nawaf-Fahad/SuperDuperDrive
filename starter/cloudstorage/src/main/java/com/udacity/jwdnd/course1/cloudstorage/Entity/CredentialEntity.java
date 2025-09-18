package com.udacity.jwdnd.course1.cloudstorage.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "credentials")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CredentialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credentialid")
    private Long id;

    @Column(name = "url", length = 100, nullable = false)
    private String url;

    @Column(name = "username", length = 30, nullable = false)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userid", nullable = false)
    private UserEntity user;
}
