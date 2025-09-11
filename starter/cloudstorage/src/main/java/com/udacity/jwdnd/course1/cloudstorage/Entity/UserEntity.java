package com.udacity.jwdnd.course1.cloudstorage.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "USERS")
@Getter  @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid")
    private Long id;

    @Column(name = "username", length = 20, unique = true, nullable = false)
    private String username;

    @Column(name = "salt", nullable = false)
    private String salt;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "firstname", length = 20, nullable = false)
    private String firstName;

    @Column(name = "lastname", length = 20, nullable = false)
    private String lastName;

}
