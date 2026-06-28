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
public class AnalysisResultDto {

    private Long id;
    private String language;
    private String sourceCode;
    private String summary;
    private List<LineExplanation> lineExplanations;
    private List<FunctionExplanation> functionExplanations;
    private List<VariableDescription> variableDescriptions;
    private String algorithmExplanation;
    private String timeComplexity;
    private String spaceComplexity;
    private List<String> optimizationSuggestions;
    private List<BugReport> bugs;
    private List<String> refactoringSuggestions;
    private String refactoredCode;
    private List<String> learningResources;
    private String analysisType;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineExplanation {
        private int lineNumber;
        private String code;
        private String explanation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FunctionExplanation {
        private String name;
        private String explanation;
        private String returnType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariableDescription {
        private String name;
        private String type;
        private String purpose;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BugReport {
        private String severity;
        private String type;
        private String description;
        private int lineNumber;
        private String suggestion;
    }
}
