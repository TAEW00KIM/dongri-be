package com.hufs.dongri.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hufs.dongri.domain.enums.ClubRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "memberships",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "club_id"}) // 한 유저는 한 동아리에 한 번만 가입
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어느 유저가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    // 어느 동아리에
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    @JsonBackReference
    private Club club;

    // 어떤 역할로 (MEMBER or ADMIN)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClubRole clubRole;

    // 이 멤버가 작성한 공지/일정 목록
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Notice> authoredNotices = new ArrayList<>();
}