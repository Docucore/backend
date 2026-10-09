package com.swucapstone.docucore.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaperListDto {
    private final Long id;
    private final String title;
    private final String author;
}
