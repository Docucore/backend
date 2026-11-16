package com.swucapstone.docucore.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HighLightRequest {

    private int start;

    private int end;
}