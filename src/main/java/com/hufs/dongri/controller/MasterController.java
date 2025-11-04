package com.hufs.dongri.controller;

import com.hufs.dongri.dto.application.ApplicationDto;
import com.hufs.dongri.dto.application.RejectDto;
import com.hufs.dongri.dto.master.PendingUserDto;
import com.hufs.dongri.global.response.ApiResult;
import com.hufs.dongri.service.MasterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master")
@RequiredArgsConstructor
@Tag(name = "4. Master (동연/최고관리자)", description = "동아리연합회(MASTER) 전용 API")
@SecurityRequirement(name = "Authorization")
@PreAuthorize("hasRole('MASTER')")
public class MasterController {

    private final MasterService masterService;

    @GetMapping("/applications")
    @Operation(summary = "[학생 승급] 학생 운영진 승급 신청 목록 조회",
            description = "승인 대기 중(PENDING)인 학생(@hufs.ac.kr)들의 운영진 승급 신청 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ApplicationDto.class)))),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    public ResponseEntity<ApiResult<List<ApplicationDto>>> getPendingApplications() {
        List<ApplicationDto> data = masterService.getPendingApplications();
        return ResponseEntity.ok(ApiResult.success(HttpStatus.OK.value(), "학생 승급 신청 목록 조회 성공", data));
    }

    @PostMapping("/applications/{applicationId}/approve")
    @Operation(summary = "[학생 승급] 학생 운영진 승급 신청 '승인'",
            description = "학생의 승급 신청을 '승인(APPROVED)' 상태로 변경하고, 해당 유저에게 ADMIN 권한을 부여합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "승인 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 신청서")
    })
    public ResponseEntity<ApiResult<Void>> approveApplication(
            @Parameter(description = "승인할 신청서의 ID") @PathVariable Long applicationId
    ) {
        masterService.approveApplication(applicationId);
        return ResponseEntity.ok(ApiResult.success(HttpStatus.OK.value(), "학생 승급 신청을 승인했습니다."));
    }

    @PostMapping("/applications/{applicationId}/reject")
    @Operation(summary = "[학생 승급] 학생 운영진 승급 신청 '거절'",
            description = "학생의 승급 신청을 '거절(REJECTED)' 상태로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "거절 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 신청서")
    })
    public ResponseEntity<ApiResult<Void>> rejectApplication(
            @Parameter(description = "거절할 신청서의 ID") @PathVariable Long applicationId,
            @RequestBody RejectDto dto
    ) {
        masterService.rejectApplication(applicationId, dto);
        return ResponseEntity.ok(ApiResult.success(HttpStatus.OK.value(), "학생 승급 신청을 거절했습니다."));
    }

    @GetMapping("/accounts/pending")
    @Operation(summary = "[공용 계정] 폼 가입 계정 승인 대기 목록",
            description = "폼으로 가입하여 승인 대기(PENDING) 중인 공용 계정 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PendingUserDto.class)))),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    public ResponseEntity<ApiResult<List<PendingUserDto>>> getPendingAccounts() {
        List<PendingUserDto> data = masterService.getPendingAccounts();
        return ResponseEntity.ok(ApiResult.success(HttpStatus.OK.value(), "공용 계정 승인 대기 목록 조회 성공", data));
    }

    @PostMapping("/accounts/{userId}/approve")
    @Operation(summary = "[공용 계정] 공용 계정 승인 및 ADMIN 임명",
            description = "대기 중인 계정을 'ACTIVE' 상태로 변경하고, 특정 동아리의 'ADMIN'으로 즉시 임명합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "승인 및 임명 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자 또는 동아리")
    })
    public ResponseEntity<ApiResult<Void>> approveClubAccount(
            @Parameter(description = "승인할 계정의 ID") @PathVariable Long userId,
            @Parameter(description = "ADMIN으로 임명할 동아리 ID") @RequestParam Long clubId
    ) {
        masterService.approveClubAccount(userId, clubId);
        return ResponseEntity.ok(ApiResult.success(HttpStatus.OK.value(), "계정 승인 및 운영진 임명 완료"));
    }
}