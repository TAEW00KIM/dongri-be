package com.hufs.dongri.controller;

import com.hufs.dongri.dto.join.JoinApplicationRequestDto;
import com.hufs.dongri.global.exception.CustomException;
import com.hufs.dongri.global.exception.code.ErrorCode;
import com.hufs.dongri.global.response.CustomResponse;
import com.hufs.dongri.global.security.AuthenticatedUser;
import com.hufs.dongri.service.JoinApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "6. Join Application (학생 회원 가입 신청)", description = "학생(USER)이 동아리에 '회원'으로 가입 신청하는 API (UC-005)")
@SecurityRequirement(name = "Authorization")
public class JoinApplicationController {

    private final JoinApplicationService joinApplicationService;

    private Long getUserId(AuthenticatedUser user) {
        if (user == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return user.getUserId();
    }

    @PostMapping("/clubs/{clubId}/apply")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "학생 동아리 회원 가입 신청 (UC-005)",
            description = "학생(USER)이 특정 동아리에 회원 가입을 신청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "가입 신청 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    { "timestamp": "2025-11-18T14:20:00", "isSuccess": true, "code": "COMMON201", "message": "성공적으로 객체를 생성했습니다.", "result": null }
                    """))),
            @ApiResponse(responseCode = "400", description = "(APP409_1) 중복 신청 / (APP409_2) 이미 회원",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class),
                            examples = {
                                    @ExampleObject(name = "중복 신청", value = """
                        { "timestamp": "2025-11-18T14:21:00", "isSuccess": false, "code": "APP409_1", "message": "이미 처리 대기 중인 신청서가 존재합니다.", "result": null }
                        """),
                                    @ExampleObject(name = "이미 회원", value = """
                        { "timestamp": "2025-11-18T14:22:00", "isSuccess": false, "code": "APP409_2", "message": "이미 해당 동아리에 가입된 회원입니다.", "result": null }
                        """)
                            })),
            @ApiResponse(responseCode = "404", description = "(CLUB404_1) 존재하지 않는 동아리",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<Void>> applyToClub(
            @AuthenticationPrincipal AuthenticatedUser user, // (수정) Principal 주입
            @Parameter(description = "가입 신청할 동아리 ID") @PathVariable Long clubId,
            @RequestBody JoinApplicationRequestDto dto
    ) {
        Long userId = getUserId(user); // (수정) ID 추출
        joinApplicationService.applyToClub(userId, clubId, dto); // (수정) ID 전달

        return ResponseEntity.status(HttpStatus.CREATED).body(CustomResponse.created(null));
    }
}