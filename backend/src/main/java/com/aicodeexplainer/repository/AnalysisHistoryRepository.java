package com.aicodeexplainer.repository;

import com.aicodeexplainer.entity.AnalysisHistory;
import com.aicodeexplainer.entity.CodeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisHistoryRepository extends JpaRepository<AnalysisHistory, Long> {

    List<AnalysisHistory> findByAnalysisOrderByViewedAtDesc(CodeAnalysis analysis);
}
