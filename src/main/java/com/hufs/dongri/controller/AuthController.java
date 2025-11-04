package com.hufs.dongri.controller;

import com.hufs.dongri.dto.auth.LoginRequestDto;
import com.hufs.dongri.dto.auth.SignUpRequestDto;
import com.hufs.dongri.dto.auth.TokenResponseDto;
import com.hufs.dongri.global.response.ApiResult;
import com.hufs.dongri.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
    @Operation(summary = "폼 회원 가입 (공용 계정용)",
            description = "동아리 공용 계정(non-@hufs.ac.kr)이 가입을 '신청'합니다. MASTER의 승인이 필요합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가입 신청 성공 (유저 ID 반환)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "400", description = "가입 실패 (이메일 중복 또는 @hufs.ac.kr 시도)")
    })
    public ResponseEntity<ApiResult<Long>> signUp(@RequestBody SignUpRequestDto requestDto) {
        Long userId = authService.signUp(requestDto);
        return ResponseEntity.ok(ApiResult.success(HttpStatus.OK.value(), "가입 신청이 완료되었습니다.", userId));
    }

    @PostMapping("/login")
    @Operation(summary = "폼 로그인 (관리자/공용계정)",
            description = "MASTER 또는 승인된 동아리 공용 계정이 이메일/비밀번호로 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공 (JWT 토큰 반환)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "로그인 실패 (자격 증명 오류)"),
            @ApiResponse(responseCode = "403", description = "승인 대기 중인 계정")
    })
    public ResponseEntity<ApiResult<TokenResponseDto>> login(@RequestBody LoginRequestDto requestDto) {
        TokenResponseDto token = authService.login(requestDto);
        return ResponseEntity.ok(ApiResult.success(HttpStatus.OK.value(), "로그인 성공", token));
    }
}