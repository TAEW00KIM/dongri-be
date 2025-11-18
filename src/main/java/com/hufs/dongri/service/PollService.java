package com.hufs.dongri.service;

import com.hufs.dongri.domain.*;
import com.hufs.dongri.dto.poll.PollSummaryDto;
import com.hufs.dongri.dto.poll.PollVoteRequest;
import com.hufs.dongri.global.exception.CustomException;
import com.hufs.dongri.global.exception.code.ErrorCode;
import com.hufs.dongri.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PollService {

    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final PollResponseRepository pollResponseRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final MembershipRepository membershipRepository;

    public List<PollSummaryDto> getPolls(Long userId, Long clubId) {
        Membership membership = checkIsMemberAndGetMembership(userId, clubId);

        List<Poll> polls = pollRepository.findByClubIdWithAuthor(clubId);

        Set<Long> votedPollIds = pollResponseRepository.findVotedPollIdsByVoter(membership);

        return polls.stream()
                .map(poll -> PollSummaryDto.from(poll, votedPollIds.contains(poll.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public void voteOnPoll(Long userId, Long pollId, PollVoteRequest dto) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new CustomException(ErrorCode.POLL_NOT_FOUND));

        PollOption option = pollOptionRepository.findById(dto.getOptionId())
                .orElseThrow(() -> new CustomException(ErrorCode.POLL_OPTION_NOT_FOUND));

        if (!option.getPoll().getId().equals(pollId)) {
            throw new CustomException(ErrorCode.POLL_OPTION_NOT_FOUND);
        }

        Membership membership = checkIsMemberAndGetMembership(userId, poll.getClub().getId());

        if (poll.getDeadline() != null && poll.getDeadline().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.POLL_DEADLINE_EXPIRED);
        }

        if (pollResponseRepository.existsByPollAndVoter(poll, membership)) {
            throw new CustomException(ErrorCode.POLL_ALREADY_VOTED);
        }

        PollResponse response = PollResponse.builder()
                .pollOption(option)
                .voter(membership)
                .build();

        pollResponseRepository.save(response);
    }

    private Membership checkIsMemberAndGetMembership(Long userId, Long clubId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        return membershipRepository.findByUserAndClub(user, club)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_MEMBER));
    }
}