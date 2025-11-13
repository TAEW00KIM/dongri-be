// src/main/java/com/hufs/dongri/controller/JoinApplicationController.java (신규 파일)
package com.hufs.dongri.controller;

import com.hufs.dongri.dto.join.JoinApplicationRequestDto;
import com.hufs.dongri.global.exception.ErrorResponse;
import com.hufs.dongri.global.response.ApiResult;
import com.hufs.dongri.service.JoinApplicationService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "6. Join Application (학생 회원 가입 신청)", description = "학생(USER)이 동아리에 '회원'으로 가입 신청하는 API")
@SecurityRequirement(name = "Authorization")
public class JoinApplicationController {

    private final JoinApplicationService joinApplicationService;

    @PostMapping("/clubs/{clubId}/apply")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "학생 동아리 회원 가입 신청 (UC-005)",
            description = "학생(USER)이 특정 동아리에 회원 가입을 신청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가입 신청 성공"),
            @ApiResponse(responseCode = "400", description = "중복 신청 (이미 대기 중이거나 가입됨)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 동아리")
    })
    public ResponseEntity<ApiResult<Void>> applyToClub(
            @Parameter(description = "가입 신청할 동아리 ID") @PathVariable Long clubId,
            @RequestBody JoinApplicationRequestDto dto
    ) {
        joinApplicationService.applyToClub(clubId, dto);
        return ResponseEntity.ok(ApiResult.success(HttpStatus.OK.value(), "동아리 가입 신청이 완료되었습니다."));
    }
}