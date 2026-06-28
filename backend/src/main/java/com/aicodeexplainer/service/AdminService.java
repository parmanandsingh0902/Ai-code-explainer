package com.aicodeexplainer.service;

import com.aicodeexplainer.dto.AdminStatisticsDto;
import com.aicodeexplainer.dto.UserDto;
import com.aicodeexplainer.entity.Role;
import com.aicodeexplainer.entity.User;
import com.aicodeexplainer.exception.BadRequestException;
import com.aicodeexplainer.exception.ResourceNotFoundException;
import com.aicodeexplainer.repository.CodeAnalysisRepository;
import com.aicodeexplainer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final CodeAnalysisRepository analysisRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }

    public AdminStatisticsDto getStatistics() {
        long totalUsers = userRepository.count();
        long totalAnalyses = analysisRepository.count();
        long adminCount = userRepository.countByRole(Role.ADMIN);

        Map<String, Long> byLanguage = new HashMap<>();
        analysisRepository.countByLanguage().forEach(row ->
                byLanguage.put((String) row[0], (Long) row[1])
        );

        List<AdminStatisticsDto.DailyStatDto> daily = analysisRepository.countByDate().stream()
                .map(row -> AdminStatisticsDto.DailyStatDto.builder()
                        .date(row[0].toString())
                        .count((Long) row[1])
                        .build())
                .collect(Collectors.toList());

        return AdminStatisticsDto.builder()
                .totalUsers(totalUsers)
                .totalAnalyses(totalAnalyses)
                .adminCount(adminCount)
                .analysesByLanguage(byLanguage)
                .dailyAnalyses(daily)
                .build();
    }

    @Transactional
    public void deleteUser(Long userId, User admin) {
        if (userId.equals(admin.getId())) {
            throw new BadRequestException("Cannot delete your own admin account");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        userRepository.delete(user);
        log.info("Admin {} deleted user {}", admin.getEmail(), user.getEmail());
    }

    private UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .analysisCount(analysisRepository.countByUser(user))
                .build();
    }
}
