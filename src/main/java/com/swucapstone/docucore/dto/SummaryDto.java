package com.swucapstone.docucore.dto;

import com.swucapstone.docucore.entity.Paper;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class SummaryDto {

    private Long summaryId;
    private boolean main;
    private String content;
}
