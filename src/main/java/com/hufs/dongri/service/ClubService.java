package com.hufs.dongri.service;

import com.hufs.dongri.domain.Club;
import com.hufs.dongri.domain.enums.ClubCategory;
import com.hufs.dongri.dto.club.ClubDetailDto;
import com.hufs.dongri.dto.club.ClubSummaryDto;
import com.hufs.dongri.global.exception.EntityNotFoundException;
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

            // 4. 카테고리 필터
            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            // 5. 모집중 필터
            if (isRecruiting != null) {
                predicates.add(cb.equal(root.get("isRecruiting"), isRecruiting));
            }

            // 6. 키워드 검색 (OR 조건)
            if (search != null && !search.isBlank()) {
                String likePattern = "%" + search + "%";

                Predicate searchOrPredicate = cb.or(
                        cb.like(root.get("name"), likePattern),
                        cb.like(root.get("shortDescription"), likePattern),
                        cb.like(root.get("introduction"), likePattern)
                );
                predicates.add(searchOrPredicate);
            }

            // 7. cb.and(...)는 'jakarta.persistence.criteria.Predicate' 배열을 받음
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 8. 완성된 Specification으로 Repository 호출
        return clubRepository.findAll(spec)
                .stream()
                .map(club -> new ClubSummaryDto(
                        club.getId(),
                        club.getName(),
                        club.getCategory(),
                        club.getShortDescription(),
                        club.getLogoImageUrl(),
                        club.isRecruiting()
                ))
                .collect(Collectors.toList());
    }

    public ClubDetailDto getClubById(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("해당 동아리를 찾을 수 없습니다. ID: " + clubId));

        return new ClubDetailDto(
                club.getId(),
                club.getName(),
                club.getCategory(),
                club.getShortDescription(),
                club.getIntroduction(),
                club.getActivitySchedule(),
                club.getFee(),
                club.isRecruiting(),
                club.getLogoImageUrl()
        );
    }
}