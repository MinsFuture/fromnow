package com.knu.fromnow.api.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String identifier;

    private String profileName;

    private LocalDateTime createdTime;

    @Builder
    public Member(Role role, String identifier, String profileName) {
        this.role = role;
        this.identifier = identifier;
        this.profileName = profileName;
        this.createdTime = LocalDateTime.now();
    }
}
