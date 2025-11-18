package com.hufs.dongri.controller;

import com.hufs.dongri.dto.application.RejectDto;
import com.hufs.dongri.dto.join.JoinApplicationDto;
import com.hufs.dongri.dto.notice.NoticeRequestDto;
import com.hufs.dongri.dto.operator.AttendanceCheckRequest;
import com.hufs.dongri.dto.operator.FeeStatusUpdateRequest;
import com.hufs.dongri.dto.operator.OperatorMemberResponse;
import com.hufs.dongri.dto.operator.PollCreateRequest;
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
@RequestMapping("/api/operator/clubs")
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

    @GetMapping("/{clubId}/members")
    @Operation(summary = "[UC-009] 회원 명단 조회",
            description = "운영진이 본인 동아리의 전체 회원(운영진 포함) 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-11-18T15:30:00",
                      "isSuccess": true,
                      "code": "COMMON200",
                      "message": "성공적으로 요청을 수행했습니다.",
                      "result": [
                        {
                          "membershipId": 1,
                          "userId": 1,
                          "name": "김운영",
                          "studentId": "202000001",
                          "major": "컴퓨터공학부",
                          "clubRole": "ROLE_ADMIN"
                        },
                        {
                          "membershipId": 2,
                          "userId": 3,
                          "name": "박회원",
                          "studentId": "202200001",
                          "major": "GFLT",
                          "clubRole": "ROLE_MEMBER"
                        }
                      ]
                    }
                    """))),
            @ApiResponse(responseCode = "403", description = "(OPER403_1) 해당 동아리 운영진이 아님",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "404", description = "(CLUB404_1) 동아리를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<List<OperatorMemberResponse>>> getClubMembers(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "관리할 동아리 ID") @PathVariable Long clubId
    ) {
        Long userId = getUserId(user);
        List<OperatorMemberResponse> data = operatorService.getClubMembers(userId, clubId);
        return ResponseEntity.ok(CustomResponse.ok(data));
    }

    @GetMapping("/{clubId}/join-applications")
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

    @PostMapping("/{clubId}/join-applications/{applicationId}/approve")
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

    @PostMapping("/{clubId}/join-applications/{applicationId}/reject")
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

    @PostMapping("/{clubId}/notices")
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

    @PostMapping("/{clubId}/notices/{noticeId}/attendances")
    @Operation(summary = "[UC-009] 출석 체크 (생성/수정)",
            description = "운영진이 특정 일정(noticeId)에 대해 특정 회원(membershipId)의 출석 상태를 기록(Upsert)합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "출석 체크 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    { "timestamp": "2025-11-18T15:40:00", "isSuccess": true, "code": "COMMON200", "message": "성공적으로 요청을 수행했습니다.", "result": null }
                    """))),
            @ApiResponse(responseCode = "400", description = "(OPER400_1) 공지사항에 출석 시도",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음 (운영진 아님 / 다른 동아리 자원 접근)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "404", description = "멤버십 또는 일정을 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<Void>> checkAttendance(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "관리할 동아리 ID") @PathVariable Long clubId,
            @Parameter(description = "출석 대상 일정 ID") @PathVariable Long noticeId,
            @RequestBody @Valid AttendanceCheckRequest dto
    ) {
        Long userId = getUserId(user);
        operatorService.checkAttendance(userId, clubId, noticeId, dto);
        return ResponseEntity.ok(CustomResponse.ok(null));
    }

    @PatchMapping("/{clubId}/fees/{paymentId}")
    @Operation(summary = "[UC-009] 회비 납부 상태 변경",
            description = "운영진이 특정 회원의 회비 납부 내역(paymentId) 상태를 (미납 -> 납부완료) 등으로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상태 변경 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    { "timestamp": "2025-11-18T15:45:00", "isSuccess": true, "code": "COMMON200", "message": "성공적으로 요청을 수행했습니다.", "result": null }
                    """))),
            @ApiResponse(responseCode = "403", description = "권한 없음 (운영진 아님 / 다른 동아리 자원 접근)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "404", description = "(OPER404_3) 회비 납부 내역을 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<Void>> updateFeeStatus(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "관리할 동아리 ID") @PathVariable Long clubId,
            @Parameter(description = "관리할 회비 내역 ID") @PathVariable Long paymentId,
            @RequestBody @Valid FeeStatusUpdateRequest dto
    ) {
        Long userId = getUserId(user);
        operatorService.updateFeeStatus(userId, clubId, paymentId, dto);
        return ResponseEntity.ok(CustomResponse.ok(null));
    }

    @PostMapping("/{clubId}/polls")
    @Operation(summary = "[UC-010] 내부 투표/설문 생성",
            description = "운영진이 동아리 내부 투표(설문)를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "투표 생성 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    { "timestamp": "2025-11-18T15:50:00", "isSuccess": true, "code": "COMMON201", "message": "성공적으로 객체를 생성했습니다.", "result": null }
                    """))),
            @ApiResponse(responseCode = "400", description = "입력값 오류 (제목, 선택지 2개 미만, 과거 마감일 등)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음 (운영진 아님)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "404", description = "동아리 또는 사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<Void>> createPoll(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "관리할 동아리 ID") @PathVariable Long clubId,
            @RequestBody @Valid PollCreateRequest dto
    ) {
        Long userId = getUserId(user);
        operatorService.createPoll(userId, clubId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomResponse.created(null));
    }
}