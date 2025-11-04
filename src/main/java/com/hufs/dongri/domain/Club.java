package com.hufs.dongri.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hufs.dongri.domain.enums.ClubCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clubs")
@Getter @Setter
@NoArgsConstructor
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private ClubCategory category;

    @Column(length = 100)
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    private String activitySchedule;

    private String fee;

    private boolean isRecruiting;

    private String logoImageUrl;

    // 1. 이 동아리에 속한 멤버십 목록
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Membership> memberships = new ArrayList<>();

    // 2. 이 동아리로 접수된 운영진 신청서 목록
    @OneToMany(mappedBy = "targetClub", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<AdminApplication> applications = new ArrayList<>();
}