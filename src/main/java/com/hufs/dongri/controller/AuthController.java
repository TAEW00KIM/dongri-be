package com.hufs.dongri.controller;

import com.hufs.dongri.dto.auth.LoginRequestDto;
import com.hufs.dongri.dto.auth.SignUpRequestDto;
import com.hufs.dongri.dto.auth.TokenResponseDto;
import com.hufs.dongri.global.response.CustomResponse;
import com.hufs.dongri.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "1. Authentication (폼 인증)", description = "관리자/공용계정 폼 로그인 및 가입 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "폼 회원 가입 (공용 계정용) (UC-002)",
            description = "동아리 공용 계정(non-@example.com)이 가입을 '신청'합니다. MASTER의 승인이 필요합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "가입 신청 성공 (유저 ID 반환)",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    { "timestamp": "2025-11-18T14:00:00", "isSuccess": true, "code": "COMMON201", "message": "성공적으로 객체를 생성했습니다.", "result": 12 }
                    """))),
            @ApiResponse(responseCode = "400", description = "(AUTH400_1) 학생 계정 가입 시도 / (AUTH409_1) 이메일 중복",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class),
                            examples = {
                                    @ExampleObject(name = "학생 계정 가입 시도", value = """
                        { "timestamp": "2025-11-18T14:01:00", "isSuccess": false, "code": "AUTH400_1", "message": "학생은 [Google로 로그인]을 이용해주세요.", "result": null }
                        """),
                                    @ExampleObject(name = "이메일 중복", value = """
                        { "timestamp": "2025-11-18T14:02:00", "isSuccess": false, "code": "AUTH409_1", "message": "이미 사용 중인 이메일입니다.", "result": null }
                        """)
                            }))
    })
    public ResponseEntity<CustomResponse<Long>> signUp(@RequestBody SignUpRequestDto requestDto) {
        Long userId = authService.signUp(requestDto);
        // (수정) ApiResult.success -> CustomResponse.created
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomResponse.created(userId));
    }

    @PostMapping("/login")
    @Operation(summary = "폼 로그인 (관리자/공용계정) (UC-003)",
            description = "MASTER 또는 승인된 동아리 공용 계정이 이메일/비밀번호로 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공 (JWT 토큰 반환)",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    { "timestamp": "2025-11-18T14:03:00", "isSuccess": true, "code": "COMMON200", "message": "성공적으로 요청을 수행했습니다.", "result": { "accessToken": "eyJh..." } }
                    """))),
            @ApiResponse(responseCode = "401", description = "로그인 실패 (자격 증명 오류)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    { "timestamp": "2025-11-18T14:04:00", "isSuccess": false, "code": "COMMON401", "message": "인증이 필요합니다.", "result": null }
                    """))),
            @ApiResponse(responseCode = "403", description = "(AUTH403_1) 승인 대기 중인 계정",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    { "timestamp": "2025-11-18T14:05:00", "isSuccess": false, "code": "AUTH403_1", "message": "아직 승인 대기 중인 계정입니다.", "result": null }
                    """)))
    })
    public ResponseEntity<CustomResponse<TokenResponseDto>> login(@RequestBody LoginRequestDto requestDto) {
        TokenResponseDto token = authService.login(requestDto);
        return ResponseEntity.ok(CustomResponse.ok(token));
    }
}