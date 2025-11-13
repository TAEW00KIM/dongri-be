package com.hufs.dongri.controller;

import com.hufs.dongri.dto.application.RejectDto;
import com.hufs.dongri.dto.join.JoinApplicationDto;
import com.hufs.dongri.global.exception.ErrorResponse;
import com.hufs.dongri.global.response.ApiResult;
import com.hufs.dongri.service.OperatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operator/clubs/{clubId}") // 1. 공통으로 clubId를 받음
@RequiredArgsConstructor
@Tag(name = "7. Operator (동아리 운영진)", description = "동아리 운영진(ClubRole.ADMIN) 전용 API")
@SecurityRequirement(name = "Authorization")
public class OperatorController {

    private final OperatorService operatorService;

    @GetMapping("/join-applications")
    @Operation(summary = "[UC-007] 회원 가입 신청 대기 목록 조회",
            description = "운영진이 본인 동아리의 '대기중'인 회원 가입 신청 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "해당 동아리 운영진이 아님"),
            @ApiResponse(responseCode = "404", description = "동아리를 찾을 수 없음")
    })
    public ResponseEntity<ApiResult<List<JoinApplicationDto>>> getPendingJoinApplications(
            @Parameter(description = "관리할 동아리 ID") @PathVariable Long clubId
    ) {
        List<JoinApplicationDto> data = operatorService.getPendingJoinApplications(clubId);
        return ResponseEntity.ok(ApiResult.success(HttpStatus.OK.value(), "가입 신청 대기 목록 조회 성공", data));
    }

    @PostMapping("/join-applications/{applicationId}/approve")
    @Operation(summary = "[UC-007] 회원 가입 신청 '승인'",
            description = "운영진이 가입 신청을 승인합니다. 대상은 정식 회원(MEMBER)이 됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "승인 성공"),
            @ApiResponse(responseCode = "400", description = "이미 처리된 신청서"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "동아리 또는 신청서를 찾을 수 없음")
    })
    public ResponseEntity<ApiResult<Void>> approveJoinApplication(
            @Parameter(description = "관리할 동아리 ID") @PathVariable Long clubId,
            @Parameter(description = "처리할 신청서 ID") @PathVariable Long applicationId
    ) {
        operatorService.approveJoinApplication(clubId, applicationId);
        return ResponseEntity.ok(ApiResult.success(HttpStatus.OK.value(), "가입 신청을 승인했습니다."));
    }

    @PostMapping("/join-applications/{applicationId}/reject")
    @Operation(summary = "[UC-007] 회원 가입 신청 '거절'",
            description = "운영진이 가입 신청을 거절합니다. (거절 사유 DTO 재사용)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "거절 성공"),
            @ApiResponse(responseCode = "400", description = "이미 처리된 신청서"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "동아리 또는 신청서를 찾을 수 없음")
    })
    public ResponseEntity<ApiResult<Void>> rejectJoinApplication(
            @Parameter(description = "관리할 동아리 ID") @PathVariable Long clubId,
            @Parameter(description = "처리할 신청서 ID") @PathVariable Long applicationId,
            @RequestBody RejectDto dto // Master가 쓰던 DTO 재사용
    ) {
        operatorService.rejectJoinApplication(clubId, applicationId, dto);
        return ResponseEntity.ok(ApiResult.success(HttpStatus.OK.value(), "가입 신청을 거절했습니다."));
    }
}