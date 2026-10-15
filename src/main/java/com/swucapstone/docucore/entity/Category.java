package com.swucapstone.docucore.entity;

public enum CATEGORY {
    AI("인공지능"),
    ML("머신러닝"),
    CL("전산언어학"),
    IR("정보검색"),
    CV("컴퓨터비전"),
    OTHER("기타");

    private final String label;
    CATEGORY(String label) { this.label = label; }
    public String getLabel() { return label; }
}