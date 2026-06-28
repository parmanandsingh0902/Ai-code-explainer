package com.aicodeexplainer.controller;

import com.aicodeexplainer.dto.AdminStatisticsDto;
import com.aicodeexplainer.dto.ApiResponse;
import com.aicodeexplainer.dto.DashboardDto;
import com.aicodeexplainer.dto.UserDto;
import com.aicodeexplainer.entity.User;
import com.aicodeexplainer.security.CustomUserDetailsService;
import com.aicodeexplainer.service.AdminService;
import com.aicodeexplainer.service.AnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DashboardAndAdminController {

    private final AnalysisService analysisService;
    private final AdminService adminService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/api/dashboard")
    @Operation(summary = "Get user dashboard data")
    @Tag(name = "Dashboard")
    public ResponseEntity<ApiResponse<DashboardDto>> dashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userDetailsService.loadUserEntityByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(analysisService.getDashboard(user)));
    }

    @GetMapping("/api/admin/users")
    @Operation(summary = "List all users (Admin only)")
    @Tag(name = "Admin")
    public ResponseEntity<ApiResponse<List<UserDto>>> getUsers() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllUsers()));
    }

    @GetMapping("/api/admin/statistics")
    @Operation(summary = "Get system statistics (Admin only)")
    @Tag(name = "Admin")
    public ResponseEntity<ApiResponse<AdminStatisticsDto>> getStatistics() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getStatistics()));
    }

    @DeleteMapping("/api/admin/user/{id}")
    @Operation(summary = "Delete a user (Admin only)")
    @Tag(name = "Admin")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User admin = userDetailsService.loadUserEntityByEmail(userDetails.getUsername());
        adminService.deleteUser(id, admin);
        return ResponseEntity.ok(ApiResponse.success("User deleted", null));
    }
}
