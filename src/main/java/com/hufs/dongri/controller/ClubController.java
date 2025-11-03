// ClubController.java
package com.hufs.dongri.controller;

import com.hufs.dongri.dto.club.ClubDetailDto;
import com.hufs.dongri.dto.club.ClubSummaryDto;
import com.hufs.dongri.service.ClubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
@Tag(name = "2. Club (동아리 조회)", description = "동아리 정보 조회 API (로그인 불필요)")
public class ClubController {

    private final ClubService clubService;

    @GetMapping
    @Operation(summary = "전체 동아리 목록 조회",
            description = "로그인 없이 누구나 전체 동아리 목록을 조회할 수 있습니다. (요약 정보)")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    public ResponseEntity<List<ClubSummaryDto>> getAllClubs() {
        return ResponseEntity.ok(clubService.getAllClubs());
    }

    @GetMapping("/{clubId}")
    @Operation(summary = "특정 동아리 상세 조회",
            description = "로그인 없이 누구나 특정 동아리의 상세 정보를 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClubDetailDto.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 동아리")
    })
    public ResponseEntity<ClubDetailDto> getClubById(
            @Parameter(description = "조회할 동아리의 ID") @PathVariable Long clubId
    ) {
        return ResponseEntity.ok(clubService.getClubById(clubId));
    }
}