// ClubController.java (신규 또는 수정)
package com.hufs.dongri.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
@Tag(name = "2. Club (동아리 조회)", description = "동아리 정보 조회 API (로그인 불필요)")
public class ClubController {

    // (ClubService 주입 필요)

    @GetMapping
    @Operation(summary = "전체 동아리 목록 조회", description = "로그인 없이 누구나 전체 동아리 목록을 조회할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    public ResponseEntity<List<?>> getAllClubs() {
        // (ClubDto 리스트 반환 로직)
        // List<ClubDto> clubs = clubService.findAll();
        // return ResponseEntity.ok(clubs);
        return null;
    }

    // (GET /api/clubs/{clubId} - 동아리 상세 조회 API 등...)
}