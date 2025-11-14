package com.hufs.dongri.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hufs.dongri.domain.enums.NoticeType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
@Getter
@Setter // (편의를 위해 Setter 열어둠, 원래는 Service에서 관리)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // @CreatedDate 사용
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어느 동아리의 공지/일정인가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    @JsonBackReference
    private Club club;

    // 누가 작성했는가 (유저가 아니라 "동아리 회원" 자격)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_membership_id")
    @JsonBackReference
    private Membership author;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeType type; // NOTICE 또는 EVENT

    @Column
    private LocalDateTime eventDate; // type이 EVENT일 경우 일정 날짜

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Notice(Club club, Membership author, String title, String content, NoticeType type, LocalDateTime eventDate) {
        this.club = club;
        this.author = author;
        this.title = title;
        this.content = content;
        this.type = type;
        this.eventDate = eventDate;
    }
}