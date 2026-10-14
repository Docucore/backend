package com.swucapstone.docucore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "section")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paper_id", nullable = false)
    private Paper paper;

    private Enum<SEC_TYPE> section_type;
    private String section_title;
    private int section_order;
    private String content;
    private String content_ko; // 국문 번역본

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "summary_id")
    private Summary summary;
}
