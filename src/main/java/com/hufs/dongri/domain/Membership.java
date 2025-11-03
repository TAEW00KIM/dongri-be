package com.hufs.dongri.domain;

import com.hufs.dongri.domain.enums.ClubRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "memberships",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "club_id"}) // 한 유저는 한 동아리에 한 번만 가입
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어느 유저가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 어느 동아리에
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    // 어떤 역할로 (MEMBER or ADMIN)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClubRole clubRole;
}