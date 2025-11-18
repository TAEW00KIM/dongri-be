package com.hufs.dongri.controller;

import com.hufs.dongri.dto.poll.PollSummaryDto;
import com.hufs.dongri.dto.poll.PollVoteRequest;
import com.hufs.dongri.global.exception.CustomException;
import com.hufs.dongri.global.exception.code.ErrorCode;
import com.hufs.dongri.global.response.CustomResponse;
import com.hufs.dongri.global.security.AuthenticatedUser;
import com.hufs.dongri.service.PollService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "8. Poll (학생 투표 조회/응답)", description = "학생(MEMBER)이 동아리 투표를 조회하고 응답하는 API")
@SecurityRequirement(name = "Authorization")
public class PollController {

    private final PollService pollService;

    private Long getUserId(AuthenticatedUser user) {
        if (user == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return user.getUserId();
    }

    @GetMapping("/clubs/{clubId}/polls")
    @Operation(summary = "[UC-011] 동아리 내 투표 목록 조회",
            description = "학생이 특정 동아리(clubId)의 투표 목록을 조회합니다. (내가 투표했는지 여부 포함)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "투표 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    { "timestamp": "2025-11-18T16:10:00", "isSuccess": true, "code": "COMMON200", "message": "성공적으로 요청을 수행했습니다.", "result": [ { "pollId": 1, "title": "정기공연 뒷풀이 참여 조사", "authorName": "김운영", "deadline": "2025-11-20T18:00:00", "isAnonymous": false, "hasVoted": true }, ... ] }
                    """))),
            @ApiResponse(responseCode = "403", description = "(POLL403_1) 해당 동아리 회원이 아님",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "404", description = "(USER404_1) 유저 없음 / (CLUB404_1) 동아리 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<List<PollSummaryDto>>> getPolls(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "조회할 동아리 ID") @PathVariable Long clubId
    ) {
        Long userId = getUserId(user);
        List<PollSummaryDto> data = pollService.getPolls(userId, clubId);
        return ResponseEntity.ok(CustomResponse.ok(data));
    }

    @PostMapping("/polls/{pollId}/vote")
    @Operation(summary = "[UC-011] 투표 응답 (투표하기)",
            description = "학생이 특정 투표(pollId)의 특정 선택지(optionId)에 투표합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "투표 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class),
                            examples = @ExampleObject(value = """
                    { "timestamp": "2025-11-18T16:15:00", "isSuccess": true, "code": "COMMON200", "message": "성공적으로 요청을 수행했습니다.", "result": null }
                    """))),
            @ApiResponse(responseCode = "400", description = "(POLL400_3) 투표 마감됨",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "403", description = "(POLL403_1) 해당 동아리 회원이 아님",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "404", description = "투표 또는 선택지를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "409", description = "(POLL409_1) 이미 투표에 참여함",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<Void>> voteOnPoll(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "투표할 Poll ID") @PathVariable Long pollId,
            @RequestBody @Valid PollVoteRequest dto
    ) {
        Long userId = getUserId(user);
        pollService.voteOnPoll(userId, pollId, dto);
        return ResponseEntity.ok(CustomResponse.ok(null));
    }
}