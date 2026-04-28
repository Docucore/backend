package com.swucapstone.docucore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Paper {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String title;
    private String title_ko;
    private String author;
    private String submitter;
    private String journal_ref;
    private LocalDate publication_date;
    private int comment; // 분량
}
