package com.swucapstone.docucore.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swucapstone.docucore.entity.*;
import com.swucapstone.docucore.repository.PaperRepository;
import com.swucapstone.docucore.repository.SectionRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

@Component
@ConditionalOnProperty(
        name = "app.data-import.enabled",
        havingValue = "true"
)
public class PaperJsonImporter implements CommandLineRunner {

    private final PaperRepository paperRepository;
    private final SectionRepository sectionRepository;
    private final ObjectMapper objectMapper;

    public PaperJsonImporter(
            PaperRepository paperRepository,
            SectionRepository sectionRepository,
            ObjectMapper objectMapper
    ) {
        this.paperRepository = paperRepository;
        this.sectionRepository = sectionRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        importPapers();
    }

    private void importPapers() throws Exception {

        ClassPathResource resource = new ClassPathResource(
                "static/data/papers_with_summaries_for_mysql.json"
        );

        try (InputStream input = resource.getInputStream()) {

            PaperRecord[] records = objectMapper.readValue(
                    input,
                    PaperRecord[].class
            );

            int paperSaved = 0;
            int sectionSaved = 0;
            int summarySaved = 0;
            int skipped = 0;

            for (PaperRecord record : records) {

                // 논문 ID가 없으면 적재할 수 없음
                if (record.arxiv_id() == null
                        || record.arxiv_id().isBlank()) {
                    throw new IllegalArgumentException(
                            "arxiv_id가 없는 레코드가 있습니다."
                    );
                }

                // 기존 논문은 중복 적재하지 않음
                if (paperRepository.findByArXivId(
                        record.arxiv_id()
                ).isPresent()) {
                    skipped++;
                    continue;
                }

                PaperData data = record.paper();

                if (data == null) {
                    throw new IllegalArgumentException(
                            "논문 메타데이터가 없습니다: "
                                    + record.arxiv_id()
                    );
                }

                // 1. Paper 저장
                Paper paper = Paper.builder()
                        .arXivId(record.arxiv_id())
                        .title(data.title())
                        .title_ko(
                                data.title_ko() == null
                                        || data.title_ko().isBlank()
                                        ? data.title()
                                        : data.title_ko()
                        )
                        .author(
                                data.author() == null
                                        || data.author().isBlank()
                                        ? "Unknown"
                                        : data.author()
                        )
                        .submitter(data.submitter())
                        .journal_ref(data.journal_ref())
                        .publicationDate(
                                data.publicationDate() == null
                                        || data.publicationDate().isBlank()
                                        ? LocalDate.of(1970, 1, 1)
                                        : LocalDate.parse(
                                        data.publicationDate()
                                )
                        )
                        .comment(data.comment())
                        .views(data.views())
                        .category(toCategory(data.category()))
                        .build();

                Paper savedPaper = paperRepository.save(paper);
                paperSaved++;

                // 2. Section과 각 Section의 Summary 저장
                List<SectionData> sections = record.sections();

                if (sections == null) {
                    continue;
                }

                for (SectionData sectionData : sections) {

//                    if (sectionData.summary() == null
//                            || sectionData.summary().content() == null
//                            || sectionData.summary().content().isBlank()) {
////                        throw new IllegalArgumentException(
////                                "섹션 요약이 없습니다: "
////                                        + record.arxiv_id()
////                                        + " / "
////                                        + sectionData.section_title()
////                        );
//                    }

// 각 Section에 대응하는 Summary 생성
                    SummaryData summaryData = sectionData.summary();

                    Summary summary = Summary.builder()
                            .main(summaryData != null && summaryData.main())
                            .content(
                                    summaryData == null || summaryData.content() == null
                                            ? ""
                                            : summaryData.content()
                            )
                            .build();


                    // Section이 Summary를 소유하고 저장을 전파
                    Section section = Section.builder()
                            .paper(savedPaper)
                            .sectionTitle(
                                    sectionData.section_title()
                            )
                            .sectionOrder(
                                    sectionData.section_order()
                            )
                            .sectionType(
                                    SEC_TYPE.valueOf(
                                            sectionData.section_type()
                                    )
                            )
                            .content(sectionData.content() == null ? "" : sectionData.content())
                            .content_ko(sectionData.content_ko())
                            .summary(summary)
                            .build();

                    // CascadeType.ALL로 Summary도 함께 저장됨
                    sectionRepository.save(section);

                    sectionSaved++;
                    summarySaved++;
                }
            }

            System.out.println("===== 데이터 적재 결과 =====");
            System.out.println("저장된 논문 수: " + paperSaved);
            System.out.println("저장된 섹션 수: " + sectionSaved);
            System.out.println("저장된 요약 수: " + summarySaved);
            System.out.println("건너뛴 논문 수: " + skipped);
        }
    }

    public record PaperRecord(
            @JsonProperty("arxiv_id") String arxiv_id,
            PaperData paper,
            List<SectionData> sections
    ) {}

    public record PaperData(
            String title,
            String title_ko,
            String author,
            String submitter,
            String journal_ref,
            String publicationDate,
            int comment,
            int views,
            String category
    ) {}

    public record SectionData(
            String section_title,
            int section_order,
            String section_type,
            String content,
            String content_ko,
            SummaryData summary
    ) {}

    public record SummaryData(
            boolean main,
            String content
    ) {}

    private CATEGORY toCategory(String value) {
        if (value == null || value.isBlank()) {
            return CATEGORY.OTHER;   // OTHER 상수가 있다면
        }
        try {
            return CATEGORY.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            System.out.println("알 수 없는 카테고리: " + value);
            return CATEGORY.OTHER;
        }
    }
}