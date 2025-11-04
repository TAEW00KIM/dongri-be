package com.hufs.dongri.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hufs.dongri.domain.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admin_applications")
@Getter
@Setter
@NoArgsConstructor
public class AdminApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 신청한 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_user_id")
    @JsonBackReference
    private User applicant;

    // 신청할 동아리
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_club_id")
    @JsonBackReference
    private Club targetClub;

    // 신청 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status; // PENDING, APPROVED, REJECTED

    // 신청 사유
    @Column(columnDefinition = "TEXT")
    private String reason;
}