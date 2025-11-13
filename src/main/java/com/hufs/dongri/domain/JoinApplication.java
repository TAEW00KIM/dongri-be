package com.hufs.dongri.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hufs.dongri.domain.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "join_applications")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 신청한 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_user_id")
    @JsonBackReference
    private User user;

    // 신청할 동아리
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_club_id")
    @JsonBackReference
    private Club club;

    // 신청 상태 (PENDING, APPROVED, REJECTED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    // 신청 사유 (UC-005)
    @Column(columnDefinition = "TEXT")
    private String reason;

    @Builder
    public JoinApplication(User user, Club club, String reason) {
        this.user = user;
        this.club = club;
        this.reason = reason;
        this.status = ApplicationStatus.PENDING; // 생성 시 항상 PENDING
    }
}