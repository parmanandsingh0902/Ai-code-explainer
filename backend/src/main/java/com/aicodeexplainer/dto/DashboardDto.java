package com.aicodeexplainer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {

    private String name;
    private String email;
    private long totalAnalyses;
    private List<AnalysisSummaryDto> recentAnalyses;
    private List<LanguageStatDto> languageStats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnalysisSummaryDto {
        private Long id;
        private String language;
        private String analysisType;
        private String summaryPreview;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LanguageStatDto {
        private String language;
        private long count;
    }
}
