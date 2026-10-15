package com.swucapstone.docucore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@Entity
@Table(name = "paper")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Paper {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String title_ko;
    @Column(name = "author", columnDefinition = "LONGTEXT",nullable = false)
    private String author;
    private String submitter;
    private String journal_ref;
    @Column(name = "publication_date", nullable = false)
    private LocalDate publicationDate;
    private int comment; // 분량
    private int views = 0; // 조회수
    private String arXivId;
    @Enumerated(EnumType.STRING)
    private CATEGORY category;
}
