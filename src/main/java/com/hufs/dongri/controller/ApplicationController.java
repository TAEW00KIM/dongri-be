// ApplicationController.java
package com.hufs.dongri.controller;

import com.hufs.dongri.dto.application.ApplicationRequestDto;
import com.hufs.dongri.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "3. Application (학생 운영진 신청)", description = "학생(@hufs.ac.kr)이 동아리 운영진(ADMIN) 권한을 신청하는 API")
@SecurityRequirement(name = "Authorization") // 이 컨트롤러의 모든 API는 JWT 인증 필요
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @PreAuthorize("hasRole('USER')") // ROLE_USER만 신청 가능
    @Operation(summary = "학생 운영진 권한 승급 신청",
            description = "학생 유저(USER)가 특정 동아리의 운영진(ADMIN)이 되기 위해 승급 신청을 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신청 성공"),
            @ApiResponse(responseCode = "400", description = "중복 신청 (이미 대기 중이거나 가입됨)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (USER가 아님)"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 동아리")
    })
    public ResponseEntity<String> applyForAdmin(@RequestBody ApplicationRequestDto dto) {
        applicationService.applyForAdmin(dto);
        return ResponseEntity.ok("운영진 승급 신청이 완료되었습니다.");
    }
}