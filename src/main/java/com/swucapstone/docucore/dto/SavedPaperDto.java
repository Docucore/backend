package com.swucapstone.docucore.dto;

import com.swucapstone.docucore.entity.CATEGORY;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SavedPaperDto {
    private Long paperId;
    private String title;
    private String author;
    private String title_ko;
    private Integer running_rate;
    private String publication_date;
    private Integer view;
    private Integer save;
    private CATEGORY category;
}
