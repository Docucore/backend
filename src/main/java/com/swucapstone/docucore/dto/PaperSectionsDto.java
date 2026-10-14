package com.swucapstone.docucore.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PaperSectionsDto {

    private Long paperId;
    private List<SectionDto> sections;
}