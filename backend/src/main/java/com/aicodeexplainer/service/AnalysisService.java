package com.aicodeexplainer.service;

import com.aicodeexplainer.dto.AnalysisResultDto;
import com.aicodeexplainer.dto.CodeAnalysisRequest;
import com.aicodeexplainer.dto.DashboardDto;
import com.aicodeexplainer.entity.AnalysisHistory;
import com.aicodeexplainer.entity.CodeAnalysis;
import com.aicodeexplainer.entity.User;
import com.aicodeexplainer.exception.ResourceNotFoundException;
import com.aicodeexplainer.repository.AnalysisHistoryRepository;
import com.aicodeexplainer.repository.CodeAnalysisRepository;
import com.aicodeexplainer.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisService {

    private final CodeAnalysisRepository analysisRepository;
    private final AnalysisHistoryRepository historyRepository;
    private final CodeAnalysisService codeAnalysisService;
    private final JsonUtil jsonUtil;

    @Transactional
    public AnalysisResultDto explain(User user, CodeAnalysisRequest request) {
        AnalysisResultDto result = codeAnalysisService.explain(request.getSourceCode(), request.getLanguage());
        return saveAndReturn(user, request, result);
    }

    @Transactional
    public AnalysisResultDto review(User user, CodeAnalysisRequest request) {
        AnalysisResultDto result = codeAnalysisService.review(request.getSourceCode(), request.getLanguage());
        return saveAndReturn(user, request, result);
    }

    @Transactional
    public AnalysisResultDto analyzeComplexity(User user, CodeAnalysisRequest request) {
        AnalysisResultDto result = codeAnalysisService.analyzeComplexity(request.getSourceCode(), request.getLanguage());
        return saveAndReturn(user, request, result);
    }

    public Page<AnalysisResultDto> getHistory(User user, Pageable pageable, String language) {
        Page<CodeAnalysis> page = analysisRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        if (language != null && !language.isBlank()) {
            List<AnalysisResultDto> filtered = page.getContent().stream()
                    .filter(a -> a.getLanguage().equalsIgnoreCase(language))
                    .map(this::toDto)
                    .collect(Collectors.toList());
            return new org.springframework.data.domain.PageImpl<>(filtered, pageable, filtered.size());
        }

        return page.map(this::toDto);
    }

    @Transactional
    public AnalysisResultDto getById(User user, Long id) {
        CodeAnalysis analysis = analysisRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Analysis not found with id: " + id));

        historyRepository.save(AnalysisHistory.builder().analysis(analysis).build());
        return toDto(analysis);
    }

    @Transactional
    public void delete(User user, Long id) {
        CodeAnalysis analysis = analysisRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Analysis not found with id: " + id));
        analysisRepository.delete(analysis);
        log.info("Analysis {} deleted by user {}", id, user.getEmail());
    }

    public DashboardDto getDashboard(User user) {
        List<CodeAnalysis> recent = analysisRepository.findTop5ByUserOrderByCreatedAtDesc(user);
        long total = analysisRepository.countByUser(user);

        Map<String, Long> langCounts = new HashMap<>();
        analysisRepository.findByUserOrderByCreatedAtDesc(user, Pageable.unpaged())
                .forEach(a -> langCounts.merge(a.getLanguage(), 1L, Long::sum));

        return DashboardDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .totalAnalyses(total)
                .recentAnalyses(recent.stream().map(a -> DashboardDto.AnalysisSummaryDto.builder()
                        .id(a.getId())
                        .language(a.getLanguage())
                        .analysisType(a.getAnalysisType())
                        .summaryPreview(truncate(a.getExplanation(), 100))
                        .createdAt(a.getCreatedAt())
                        .build()).collect(Collectors.toList()))
                .languageStats(langCounts.entrySet().stream()
                        .map(e -> DashboardDto.LanguageStatDto.builder()
                                .language(e.getKey())
                                .count(e.getValue())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private AnalysisResultDto saveAndReturn(User user, CodeAnalysisRequest request, AnalysisResultDto result) {
        try {
            CodeAnalysis entity = CodeAnalysis.builder()
                    .user(user)
                    .language(request.getLanguage())
                    .sourceCode(request.getSourceCode())
                    .explanation(result.getSummary())
                    .complexity(jsonUtil.toJson(Map.of(
                            "time", result.getTimeComplexity() != null ? result.getTimeComplexity() : "",
                            "space", result.getSpaceComplexity() != null ? result.getSpaceComplexity() : ""
                    )))
                    .suggestions(jsonUtil.toJson(result))
                    .analysisType(result.getAnalysisType())
                    .build();

            entity = analysisRepository.save(entity);
            result.setId(entity.getId());
            result.setCreatedAt(entity.getCreatedAt());
            log.info("Analysis saved: id={}, type={}, user={}", entity.getId(), entity.getAnalysisType(), user.getEmail());
            return result;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize analysis result", e);
        }
    }

    private AnalysisResultDto toDto(CodeAnalysis entity) {
        try {
            if (entity.getSuggestions() != null && !entity.getSuggestions().isBlank()) {
                AnalysisResultDto dto = jsonUtil.fromJson(entity.getSuggestions(), AnalysisResultDto.class);
                dto.setId(entity.getId());
                dto.setCreatedAt(entity.getCreatedAt());
                return dto;
            }
        } catch (JsonProcessingException e) {
            log.warn("Could not deserialize stored analysis {}", entity.getId());
        }

        return AnalysisResultDto.builder()
                .id(entity.getId())
                .language(entity.getLanguage())
                .sourceCode(entity.getSourceCode())
                .summary(entity.getExplanation())
                .analysisType(entity.getAnalysisType())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private String truncate(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, max) + "...";
    }
}
