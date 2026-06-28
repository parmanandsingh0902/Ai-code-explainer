package com.aicodeexplainer.controller;

import com.aicodeexplainer.dto.*;
import com.aicodeexplainer.entity.User;
import com.aicodeexplainer.security.CustomUserDetailsService;
import com.aicodeexplainer.service.AnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
@Tag(name = "Code Analysis", description = "Code explanation, review, and complexity analysis")
@SecurityRequirement(name = "bearerAuth")
public class AnalysisController {

    private final AnalysisService analysisService;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/explain")
    @Operation(summary = "Get line-by-line code explanation")
    public ResponseEntity<ApiResponse<AnalysisResultDto>> explain(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CodeAnalysisRequest request) {
        User user = userDetailsService.loadUserEntityByEmail(userDetails.getUsername());
        AnalysisResultDto result = analysisService.explain(user, request);
        return ResponseEntity.ok(ApiResponse.success("Explanation generated", result));
    }

    @PostMapping("/review")
    @Operation(summary = "Review code for bugs and improvements")
    public ResponseEntity<ApiResponse<AnalysisResultDto>> review(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CodeAnalysisRequest request) {
        User user = userDetailsService.loadUserEntityByEmail(userDetails.getUsername());
        AnalysisResultDto result = analysisService.review(user, request);
        return ResponseEntity.ok(ApiResponse.success("Review completed", result));
    }

    @PostMapping("/complexity")
    @Operation(summary = "Analyze time and space complexity")
    public ResponseEntity<ApiResponse<AnalysisResultDto>> complexity(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CodeAnalysisRequest request) {
        User user = userDetailsService.loadUserEntityByEmail(userDetails.getUsername());
        AnalysisResultDto result = analysisService.analyzeComplexity(user, request);
        return ResponseEntity.ok(ApiResponse.success("Complexity analysis completed", result));
    }

    @GetMapping("/history")
    @Operation(summary = "Get analysis history with optional language filter")
    public ResponseEntity<ApiResponse<Page<AnalysisResultDto>>> history(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String search) {
        User user = userDetailsService.loadUserEntityByEmail(userDetails.getUsername());
        Page<AnalysisResultDto> history = analysisService.getHistory(
                user, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")), language);

        if (search != null && !search.isBlank()) {
            var filtered = history.getContent().stream()
                    .filter(a -> a.getSourceCode().toLowerCase().contains(search.toLowerCase())
                            || (a.getSummary() != null && a.getSummary().toLowerCase().contains(search.toLowerCase())))
                    .toList();
            history = new org.springframework.data.domain.PageImpl<>(filtered, history.getPageable(), filtered.size());
        }

        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Reopen a previous analysis")
    public ResponseEntity<ApiResponse<AnalysisResultDto>> getById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = userDetailsService.loadUserEntityByEmail(userDetails.getUsername());
        AnalysisResultDto result = analysisService.getById(user, id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an analysis from history")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = userDetailsService.loadUserEntityByEmail(userDetails.getUsername());
        analysisService.delete(user, id);
        return ResponseEntity.ok(ApiResponse.success("Analysis deleted", null));
    }
}
