package com.hufs.dongri.service;

import com.hufs.dongri.domain.Club;
import com.hufs.dongri.domain.enums.ClubCategory;
import com.hufs.dongri.dto.club.ClubDetailDto;
import com.hufs.dongri.dto.club.ClubSummaryDto;
import com.hufs.dongri.global.exception.CustomException;
import com.hufs.dongri.global.exception.code.ErrorCode;
import com.hufs.dongri.repository.ClubRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubService {

    private final ClubRepository clubRepository;

    public List<ClubSummaryDto> getAllClubs(ClubCategory category, Boolean isRecruiting, String search) {

        Specification<Club> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (isRecruiting != null) {
                predicates.add(cb.equal(root.get("isRecruiting"), isRecruiting));
            }
            if (search != null && !search.isBlank()) {
                String likePattern = "%" + search + "%";
                Predicate searchOrPredicate = cb.or(
                        cb.like(root.get("name"), likePattern),
                        cb.like(root.get("shortDescription"), likePattern),
                        cb.like(root.get("introduction"), likePattern)
                );
                predicates.add(searchOrPredicate);
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return clubRepository.findAll(spec)
                .stream()
                .map(ClubSummaryDto::from)
                .collect(Collectors.toList());
    }

    public ClubDetailDto getClubById(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        return ClubDetailDto.from(club);
    }
}