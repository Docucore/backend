package com.swucapstone.docucore.entity;

import jakarta.persistence.*;

@Entity
public class Category {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String name_ko;
}
