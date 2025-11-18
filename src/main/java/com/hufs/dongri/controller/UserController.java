package com.hufs.dongri.controller;

import com.hufs.dongri.dto.user.UserDetailRequestDto;
import com.hufs.dongri.dto.user.UserMyClubResponse;
import com.hufs.dongri.dto.user.UserMyInfoResponse;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회 (마이페이지)",
            description = "현재 로그인한 사용자의 상세 정보(학번, 학과, 권한 등)를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-11-18T15:00:00",
                      "isSuccess": true,
                      "code": "COMMON200",
                      "message": "성공적으로 요청을 수행했습니다.",
                      "result": {
                        "userId": 1,
                        "email": "student@hufs.ac.kr",
                        "name": "김외대",
                        "studentId": "202100001",
                        "major": "컴퓨터공학부",
                        "globalRole": "ROLE_USER"
                      }
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "(COMMON401) 인증이 필요합니다.",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "404", description = "(USER404_1) 사용자를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<UserMyInfoResponse>> getMyInfo(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        Long userId = getUserId(user);
        UserMyInfoResponse myInfo = userService.getMyInfo(userId);
        return ResponseEntity.ok(CustomResponse.ok(myInfo));
    }

    @GetMapping("/me/clubs")
    @Operation(summary = "[UC-006] 내 동아리 목록 조회",
            description = "현재 로그인한 학생이 가입한 동아리 목록과 동아리 내 역할을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-11-18T15:10:00",
                      "isSuccess": true,
                      "code": "COMMON200",
                      "message": "성공적으로 요청을 수행했습니다.",
                      "result": [
                        {
                          "clubId": 1,
                          "name": "해무리",
                          "category": "PERFORMING_ARTS",
                          "shortDescription": "중앙 풍물패 해무리입니다.",
                          "logoImageUrl": "https://.../logo1.png",
                          "myClubRole": "ROLE_ADMIN"
                        },
                        {
                          "clubId": 3,
                          "name": "Hufspike",
                          "category": "TEAM_SPORTS",
                          "shortDescription": "중앙 야구 동아리",
                          "logoImageUrl": "https://.../logo3.png",
                          "myClubRole": "ROLE_MEMBER"
                        }
                      ]
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "(COMMON401) 인증이 필요합니다.",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "404", description = "(USER404_1) 사용자를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<List<UserMyClubResponse>>> getMyClubs(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        Long userId = getUserId(user);
        List<UserMyClubResponse> myClubs = userService.getMyClubs(userId);
        return ResponseEntity.ok(CustomResponse.ok(myClubs));
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