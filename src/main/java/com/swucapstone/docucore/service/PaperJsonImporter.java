package com.swucapstone.docucore.service;

import com.swucapstone.docucore.entity.*;
import com.swucapstone.docucore.repository.PaperRepository;
import com.swucapstone.docucore.repository.SectionRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "app.data-import.enabled",
        havingValue = "true"
)
public class PaperJsonImporter implements CommandLineRunner {

    private static final String JSON_PATH =
            "static/data/papers_with_summaries_for_mysql.json";

    private final PaperRepository paperRepository;
    private final SectionRepository sectionRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        log.info("========== Paper JSON Import 시작 ==========");

        // 1. JSON 파일 읽기
        ClassPathResource resource = new ClassPathResource(JSON_PATH);

        if (!resource.exists()) {
            throw new IllegalStateException(
                    "JSON 파일을 찾을 수 없습니다: " + JSON_PATH
            );
        }

        List<PaperRecord> records;

        try (InputStream inputStream = resource.getInputStream()) {
            records = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<PaperRecord>>() {}
            );
        }

        log.info("JSON에서 읽은 논문 수: {}", records.size());

        // 2. 기존 데이터 초기화
        // Section 삭제 시 연결된 Summary도 CascadeType.ALL에 의해 삭제됨
        log.info("기존 논문 데이터 초기화 시작");

        sectionRepository.deleteAll();
        paperRepository.deleteAll();

        log.info("기존 논문 데이터 초기화 완료");

        // 3. 새 JSON 데이터 삽입
        int paperCount = 0;
        int sectionCount = 0;

        for (PaperRecord record : records) {

            if (record.paper() == null) {
                throw new IllegalStateException(
                        "Paper 정보가 없습니다. arXiv ID: " + record.arxiv_id()
                );
            }

            PaperData data = record.paper();

            // 3-1. Paper 생성
            Paper paper = Paper.builder()
                    .title(data.title())
                    .title_ko(data.title_ko())
                    .author(data.author())
                    .submitter(data.submitter())
                    .journal_ref(data.journal_ref())
                    .publicationDate(data.publicationDate())
                    .comment(data.comment())
                    .views(data.views())
                    .arXivId(record.arxiv_id())
                    .category(parseCategory(data.category()))
                    .build();

            Paper savedPaper = paperRepository.save(paper);
            paperCount++;

            // 3-2. Section 및 Summary 생성
            if (record.sections() == null) {
                continue;
            }

            for (SectionData sectionData : record.sections()) {

                if (sectionData.summary() == null) {
                    throw new IllegalStateException(
                            "Summary 정보가 없습니다. arXiv ID: "
                                    + record.arxiv_id()
                                    + ", section: "
                                    + sectionData.section_title()
                    );
                }

                SummaryData summaryData = sectionData.summary();

                Summary summary = Summary.builder()
                        .main(summaryData.main())
                        .content(summaryData.content())
                        .build();

                Section section = Section.builder()
                        .paper(savedPaper)
                        .sectionType(parseSectionType(
                                sectionData.section_type()
                        ))
                        .sectionTitle(sectionData.section_title())
                        .sectionOrder(sectionData.section_order())
                        .content(sectionData.content())
                        .content_ko(sectionData.content_ko())
                        .summary(summary)
                        .build();

                sectionRepository.save(section);
                sectionCount++;
            }
        }

        log.info("========== Paper JSON Import 완료 ==========");
        log.info("삽입된 Paper 수: {}", paperCount);
        log.info("삽입된 Section 수: {}", sectionCount);
    }

    // CATEGORY 문자열을 Enum으로 변환
    private CATEGORY parseCategory(String category) {

        if (category == null || category.isBlank()) {
            return CATEGORY.OTHER;
        }

        try {
            return CATEGORY.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CATEGORY.OTHER;
        }
    }

    // SEC_TYPE 문자열을 Enum으로 변환
    private SEC_TYPE parseSectionType(String sectionType) {

        if (sectionType == null || sectionType.isBlank()) {
            return SEC_TYPE.OTHER;
        }

        try {
            return SEC_TYPE.valueOf(sectionType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SEC_TYPE.OTHER;
        }
    }

    // JSON DTO
    public record PaperRecord(
            String arxiv_id,
            PaperData paper,
            List<SectionData> sections
    ) {}

    public record PaperData(
            String title,
            String title_ko,
            String author,
            String submitter,
            String journal_ref,
            java.time.LocalDate publicationDate,
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
}