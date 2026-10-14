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

    @Column(name = "section_type")
    private SEC_TYPE sectionType;

    @Column(name = "section_title")
    private String sectionTitle;

    @Column(name = "section_order")
    private int sectionOrder;
    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;
    private String content_ko; // 국문 번역본

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "summary_id")
    private Summary summary;
}
