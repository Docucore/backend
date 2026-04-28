package com.swucapstone.docucore.entity;

import jakarta.persistence.*;

@Entity
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    private String name;

    private boolean kakao = false;
}
