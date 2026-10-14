package com.swucapstone.docucore.dto;

import com.swucapstone.docucore.entity.SEC_TYPE;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SectionDto {

    private Long sectionId;
    private String sectionTitle;
    private int sectionOrder;
    private SEC_TYPE sectionType;
    private String content;
    private String contentKo;
    private SummaryDto summary;
}
