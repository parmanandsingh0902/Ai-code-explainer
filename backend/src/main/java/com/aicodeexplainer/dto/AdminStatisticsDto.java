package com.aicodeexplainer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatisticsDto {

    private long totalUsers;
    private long totalAnalyses;
    private long adminCount;
    private Map<String, Long> analysesByLanguage;
    private List<DailyStatDto> dailyAnalyses;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyStatDto {
        private String date;
        private long count;
    }
}
