package com.swucapstone.docucore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "high_light")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class HighLight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paper_id", nullable = false)
    private Paper paper;

    private int start;
    private int end;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;
}
