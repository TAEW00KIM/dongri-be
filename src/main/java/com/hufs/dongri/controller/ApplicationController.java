package com.hufs.dongri.controller;

import com.hufs.dongri.dto.application.ApplicationRequestDto;
import com.hufs.dongri.global.exception.CustomException;
import com.hufs.dongri.global.exception.code.ErrorCode;
import com.hufs.dongri.global.response.CustomResponse;
import com.hufs.dongri.global.security.AuthenticatedUser;
import com.hufs.dongri.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "3. Application (학생 운영진 신청)", description = "학생(@hufs.ac.kr)이 동아리 운영진(ADMIN) 권한을 신청하는 API (UC-012)")
@SecurityRequirement(name = "Authorization")
public class ApplicationController {

    private final ApplicationService applicationService;

    private Long getUserId(AuthenticatedUser user) {
        if (user == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return user.getUserId();
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "학생 운영진 권한 승급 신청 (UC-012)",
            description = "학생 유저(USER)가 특정 동아리의 운영진(ADMIN)이 되기 위해 마스터에게 승급 신청을 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "신청 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    { "timestamp": "2025-11-18T14:15:00", "isSuccess": true, "code": "COMMON201", "message": "성공적으로 객체를 생성했습니다.", "result": null }
                    """))),
            @ApiResponse(responseCode = "400", description = "(APP409_1) 중복 신청 / (APP409_2) 이미 회원",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class),
                            examples = {
                                    @ExampleObject(name = "중복 신청", value = """
                        { "timestamp": "2025-11-18T14:16:00", "isSuccess": false, "code": "APP409_1", "message": "이미 처리 대기 중인 신청서가 존재합니다.", "result": null }
                        """),
                                    @ExampleObject(name = "이미 회원", value = """
                        { "timestamp": "2025-11-18T14:17:00", "isSuccess": false, "code": "APP409_2", "message": "이미 해당 동아리에 가입된 회원입니다.", "result": null }
                        """)
                            })),
            @ApiResponse(responseCode = "404", description = "(USER404_1) 유저 없음 / (CLUB404_1) 동아리 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<Void>> applyForAdmin(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody ApplicationRequestDto dto
    ) {
        Long userId = getUserId(user);
        applicationService.applyForAdmin(userId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(CustomResponse.created(null));
    }
}