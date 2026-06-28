package com.aicodeexplainer.service;

import com.aicodeexplainer.dto.AnalysisResultDto;

/**
 * Pluggable interface for code analysis providers.
 * Implementations: MockCodeAnalysisService (default), OpenAI, DeepSeek, Gemini, etc.
 */
public interface CodeAnalysisService {

    AnalysisResultDto explain(String sourceCode, String language);

    AnalysisResultDto review(String sourceCode, String language);

    AnalysisResultDto analyzeComplexity(String sourceCode, String language);
}
