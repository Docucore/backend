package com.swucapstone.docucore.dto;
import com.swucapstone.docucore.entity.HighLight;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HighLightResponse {

    private Long highlightId;
    private Long paperId;
    private Long sectionId;
    private Long userId;

    private int start;
    private int end;

    public static HighLightResponse from(HighLight highlight) {
        return HighLightResponse.builder()
                .highlightId(highlight.getId())
                .paperId(highlight.getPaper().getId())
                .sectionId(highlight.getSection().getId())
                .userId(highlight.getUser().getId())
                .start(highlight.getStart())
                .end(highlight.getEnd())
                .build();
    }
}