package com.swucapstone.docucore.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class PaperDetailDto {
    private String title;
    private String title_ko;
    private String author;
    private String submitter;
    private String journal_ref;
    private LocalDate publication_date;
    private int comment; // 분량
    private int views; // 조회수
    private int save;
}
