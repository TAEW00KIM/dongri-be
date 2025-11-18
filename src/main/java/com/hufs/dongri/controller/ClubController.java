package com.hufs.dongri.controller;

import com.hufs.dongri.domain.enums.ClubCategory;
import com.hufs.dongri.dto.club.ClubDetailDto;
import com.hufs.dongri.dto.club.ClubSummaryDto;
import com.hufs.dongri.global.response.CustomResponse;
import com.hufs.dongri.service.ClubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    @Operation(summary = "전체 동아리 목록 조회 (필터/검색) (UC-004)",
            description = "로그인 없이 누구나 전체 동아리 목록을 조회할 수 있습니다. (필터링/검색 포함)")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = CustomResponse.class),
                    examples = @ExampleObject(value = """
            { "timestamp": "2025-11-18T14:10:00", "isSuccess": true, "code": "COMMON200", "message": "성공적으로 요청을 수행했습니다.", "result": [ { "clubId": 1, "name": "해무리", "category": "PERFORMING_ARTS", "shortDescription": "풍물패입니다.", "logoImageUrl": null, "isRecruiting": true }, ... ] }
            """)))
    public ResponseEntity<CustomResponse<List<ClubSummaryDto>>> getAllClubs(
            @Parameter(description = "카테고리 필터") @RequestParam(required = false) ClubCategory category,
            @Parameter(description = "모집 중 여부 필터") @RequestParam(required = false) Boolean isRecruiting,
            @Parameter(description = "검색 키워드") @RequestParam(required = false) String search
    ) {
        List<ClubSummaryDto> data = clubService.getAllClubs(category, isRecruiting, search);
        return ResponseEntity.ok(CustomResponse.ok(data));
    }

    @GetMapping("/{clubId}")
    @Operation(summary = "특정 동아리 상세 조회 (UC-004)",
            description = "로그인 없이 누구나 특정 동아리의 상세 정보를 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                { "timestamp": "2025-11-18T14:11:00", "isSuccess": true, "code": "COMMON200", "message": "성공적으로 요청을 수행했습니다.", "result": { "clubId": 1, "name": "해무리", "category": "PERFORMING_ARTS", "shortDescription": "풍물패입니다.", "introduction": "상세소개...", "activitySchedule": "매주 수요일", "fee": "2만원", "isRecruiting": true, "logoImageUrl": null } }
                """))),
            @ApiResponse(responseCode = "404", description = "(CLUB404_1) 존재하지 않는 동아리",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                { "timestamp": "2025-11-18T14:12:00", "isSuccess": false, "code": "CLUB404_1", "message": "해당 동아리를 찾을 수 없습니다.", "result": null }
                """)))
    })
    public ResponseEntity<CustomResponse<ClubDetailDto>> getClubById(
            @Parameter(description = "조회할 동아리의 ID") @PathVariable Long clubId
    ) {
        ClubDetailDto data = clubService.getClubById(clubId);
        return ResponseEntity.ok(CustomResponse.ok(data));
    }
}