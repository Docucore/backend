package com.swucapstone.docucore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "summary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Summary {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Column(name = "is_main", nullable = false)
    private boolean main;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder
    public Summary(boolean main, String content) {
        this.main = main;
        this.content = content;
    }
}