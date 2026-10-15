package com.swucapstone.docucore.dto;

import com.swucapstone.docucore.entity.CATEGORY;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaperListDto {
    private final Long id;
    private final String title;
    private final String author;
    private final String title_ko;
    private final int view;
    private final int save;
    private CATEGORY category;
}
