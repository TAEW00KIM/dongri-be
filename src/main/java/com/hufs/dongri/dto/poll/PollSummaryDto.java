package com.hufs.dongri.dto.poll;

import com.hufs.dongri.domain.Poll;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class PollSummaryDto {

    private Long pollId;
    private String title;
    private String authorName;
    private LocalDateTime deadline;
    private boolean isAnonymous;
    private boolean hasVoted;

    public static PollSummaryDto from(Poll poll, boolean hasVoted) {
        return PollSummaryDto.builder()
                .pollId(poll.getId())
                .title(poll.getTitle())
                .authorName(poll.getAuthor().getUser().getName())
                .deadline(poll.getDeadline())
                .isAnonymous(poll.isAnonymous())
                .hasVoted(hasVoted)
                .build();
    }
}