package com.hufs.dongri.controller;

import com.hufs.dongri.dto.application.RejectDto;
import com.hufs.dongri.dto.join.JoinApplicationDto;
import com.hufs.dongri.dto.notice.NoticeRequestDto;
import com.hufs.dongri.global.exception.CustomException;
import com.hufs.dongri.global.exception.code.ErrorCode;
import com.hufs.dongri.global.response.CustomResponse;
import com.hufs.dongri.global.security.AuthenticatedUser;
import com.hufs.dongri.service.OperatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operator/clubs/{clubId}")
@RequiredArgsConstructor
@Tag(name = "7. Operator (동아리 운영진)", description = "동아리 운영진(ClubRole.ROLE_ADMIN) 전용 API")
@SecurityRequirement(name = "Authorization")
public class OperatorController {

    private final OperatorService operatorService;

    private Long getUserId(AuthenticatedUser user) {
        if (user == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return user.getUserId();
    }

    @GetMapping("/join-applications")
    @Operation(summary = "[UC-007] 회원 가입 신청 대기 목록 조회",
            description = "운영진이 본인 동아리의 '대기중'인 회원 가입 신청 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    { "timestamp": "2025-11-18T14:40:00", "isSuccess": true, "code": "COMMON200", "message": "성공적으로 요청을 수행했습니다.", "result": [ { "applicationId": 2, "status": "PENDING", "reason": "가입하고 싶습니다.", "applicantUserId": 3, "applicantName": "박학생", "applicantStudentId": "202200001", "applicantMajor": "컴퓨터공학부" }, ... ] }
                    """))),
            @ApiResponse(responseCode = "403", description = "(OPER403_1) 해당 동아리 운영진이 아님",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "404", description = "(CLUB404_1) 동아리를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<List<JoinApplicationDto>>> getPendingJoinApplications(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "관리할 동아리 ID") @PathVariable Long clubId
    ) {
        Long userId = getUserId(user);
        List<JoinApplicationDto> data = operatorService.getPendingJoinApplications(userId, clubId);

        return ResponseEntity.ok(CustomResponse.ok(data));
    }

    @PostMapping("/join-applications/{applicationId}/approve")
    @Operation(summary = "[UC-007] 회원 가입 신청 '승인'",
            description = "운영진이 가입 신청을 승인합니다. 대상은 정식 회원(MEMBER)이 됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "승인 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    { "timestamp": "2025-11-18T14:41:00", "isSuccess": true, "code": "COMMON200", "message": "성공적으로 요청을 수행했습니다.", "result": null }
                    """))),
            @ApiResponse(responseCode = "400", description = "(APP400_2) 이미 처리된 신청서",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "404", description = "동아리 또는 신청서를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<Void>> approveJoinApplication(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "관리할 동아리 ID") @PathVariable Long clubId,
            @Parameter(description = "처리할 신청서 ID") @PathVariable Long applicationId
    ) {
        Long userId = getUserId(user);
        operatorService.approveJoinApplication(userId, clubId, applicationId);

        return ResponseEntity.ok(CustomResponse.ok(null));
    }

    @PostMapping("/join-applications/{applicationId}/reject")
    @Operation(summary = "[UC-007] 회원 가입 신청 '거절'",
            description = "운영진이 가입 신청을 거절합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "거절 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    { "timestamp": "2025-11-18T14:42:00", "isSuccess": true, "code": "COMMON200", "message": "성공적으로 요청을 수행했습니다.", "result": null }
                    """))),
            @ApiResponse(responseCode = "400", description = "(APP400_2) 이미 처리된 신청서",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "404", description = "동아리 또는 신청서를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<Void>> rejectJoinApplication(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "관리할 동아리 ID") @PathVariable Long clubId,
            @Parameter(description = "처리할 신청서 ID") @PathVariable Long applicationId,
            @RequestBody RejectDto dto
    ) {
        Long userId = getUserId(user);
        operatorService.rejectJoinApplication(userId, clubId, applicationId, dto);

        return ResponseEntity.ok(CustomResponse.ok(null));
    }

    @PostMapping("/notices")
    @Operation(summary = "[UC-008] 동아리 공지/일정 등록",
            description = "운영진이 본인 동아리에 공지사항(NOTICE) 또는 일정(EVENT)을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "등록 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    { "timestamp": "2025-11-18T14:43:00", "isSuccess": true, "code": "COMMON201", "message": "성공적으로 객체를 생성했습니다.", "result": null }
                    """))),
            @ApiResponse(responseCode = "400", description = "입력값 오류 (제목, 타입 등)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "403", description = "(OPER403_1) 해당 동아리 운영진이 아님",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "404", description = "동아리 또는 사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<Void>> createNotice(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "관리할 동아리 ID") @PathVariable Long clubId,
            @RequestBody @Valid NoticeRequestDto dto
    ) {
        Long userId = getUserId(user);
        operatorService.createNotice(userId, clubId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(CustomResponse.created(null));
    }
}