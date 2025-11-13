package com.hufs.dongri.controller;

import com.hufs.dongri.domain.enums.ClubCategory;
import com.hufs.dongri.dto.club.ClubDetailDto;
import com.hufs.dongri.dto.club.ClubSummaryDto;
import com.hufs.dongri.global.exception.ErrorResponse;
import com.hufs.dongri.global.response.ApiResult;
import com.hufs.dongri.service.ClubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
@Tag(name = "2. Club (동아리 조회)", description = "동아리 정보 조회 API (로그인 불필요)")
public class ClubController {

    private final ClubService clubService;

    @GetMapping
    @Operation(summary = "전체 동아리 목록 조회 (필터/검색)", // 3. Operation 요약 수정
            description = "로그인 없이 누구나 전체 동아리 목록을 조회할 수 있습니다. (필터링/검색 포함)")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    public ApiResult<List<ClubSummaryDto>> getAllClubs(
            @Parameter(description = "카테고리 필터") @RequestParam(required = false) ClubCategory category,
            @Parameter(description = "모집 중 여부 필터") @RequestParam(required = false) Boolean isRecruiting,
            @Parameter(description = "검색 키워드") @RequestParam(required = false) String search
    ) {
        // 5. 서비스로 파라미터 전달
        List<ClubSummaryDto> data = clubService.getAllClubs(category, isRecruiting, search);
        return ApiResult.success(HttpStatus.OK.value(), "전체 동아리 목록 조회 성공", data);
    }
    @GetMapping("/{clubId}")
    @Operation(summary = "특정 동아리 상세 조회",
            description = "로그인 없이 누구나 특정 동아리의 상세 정보를 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 동아리",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ApiResult<ClubDetailDto>> getClubById(
            @Parameter(description = "조회할 동아리의 ID") @PathVariable Long clubId
    ) {
        ClubDetailDto data = clubService.getClubById(clubId);
        return ResponseEntity.ok(ApiResult.success(HttpStatus.OK.value(), "동아리 상세 조회 성공", data));
    }
}