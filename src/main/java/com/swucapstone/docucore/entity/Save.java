package com.swucapstone.docucore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "save")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Save {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "paper_id", nullable = false)
    private Paper paper;
}
