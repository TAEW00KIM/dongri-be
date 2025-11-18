package com.hufs.dongri.controller;

import com.hufs.dongri.dto.user.UserDetailRequestDto;
import com.hufs.dongri.global.exception.CustomException;
import com.hufs.dongri.global.exception.code.ErrorCode;
import com.hufs.dongri.global.response.CustomResponse;
import com.hufs.dongri.global.security.AuthenticatedUser;
import com.hufs.dongri.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "5. User (학생 정보)", description = "학생 유저(@hufs.ac.kr) 관련 API")
@SecurityRequirement(name = "Authorization")
public class UserController {

    private final UserService userService;

    private Long getUserId(AuthenticatedUser user) {
        if (user == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return user.getUserId();
    }

    @PatchMapping("/me/details")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "학생 추가 정보(학번/학과) 입력",
            description = "OAuth2로 최초 가입한 학생(USER)이 자신의 학번과 학과를 입력합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 업데이트 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    { "timestamp": "2025-11-18T14:25:00", "isSuccess": true, "code": "COMMON200", "message": "성공적으로 요청을 수행했습니다.", "result": null }
                    """))),
            @ApiResponse(responseCode = "400", description = "(USER409_1) 이미 등록된 학번",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    { "timestamp": "2025-11-18T14:26:00", "isSuccess": false, "code": "USER409_1", "message": "이미 등록된 학번입니다.", "result": null }
                    """))),
            @ApiResponse(responseCode = "404", description = "(USER404_1) 사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<Void>> updateUserDetails(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody UserDetailRequestDto dto
    ) {
        Long userId = getUserId(user);
        userService.updateUserDetails(userId, dto);

        return ResponseEntity.ok(CustomResponse.ok(null));
    }
}