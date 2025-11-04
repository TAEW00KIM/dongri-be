package com.hufs.dongri.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hufs.dongri.domain.AdminApplication;
import com.hufs.dongri.domain.Membership;
import com.hufs.dongri.domain.enums.GlobalRole;
import com.hufs.dongri.domain.enums.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // 암호화되어 저장

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String studentId;

    @Column
    private String major;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GlobalRole globalRole; // 시스템 역할 (ROLE_USER, ROLE_MASTER)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    // 1. 이 유저가 속한 동아리 목록 (Membership을 통해)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Membership> memberships = new ArrayList<>();

    // 2. 이 유저가 신청한 운영진 신청서 목록
    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<AdminApplication> applications = new ArrayList<>();
}