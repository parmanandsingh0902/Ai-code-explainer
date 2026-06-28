package com.aicodeexplainer.repository;

import com.aicodeexplainer.entity.CodeAnalysis;
import com.aicodeexplainer.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeAnalysisRepository extends JpaRepository<CodeAnalysis, Long> {

    Page<CodeAnalysis> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    List<CodeAnalysis> findTop5ByUserOrderByCreatedAtDesc(User user);

    Optional<CodeAnalysis> findByIdAndUser(Long id, User user);

    long countByUser(User user);

    @Query("SELECT c.language, COUNT(c) FROM CodeAnalysis c GROUP BY c.language")
    List<Object[]> countByLanguage();

    @Query("SELECT DATE(c.createdAt), COUNT(c) FROM CodeAnalysis c GROUP BY DATE(c.createdAt) ORDER BY DATE(c.createdAt)")
    List<Object[]> countByDate();
}
