package com.knu.fromnow.api.domain.member.entity;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import com.knu.fromnow.api.domain.like.entity.Like;
import com.knu.fromnow.api.domain.mission.entity.Mission;
import com.knu.fromnow.api.global.spec.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String photoUrl;

    private String email;

    private String profileName;

    private String refreshToken;

    private String fcmToken;

    @OneToOne(mappedBy = "member")
    private Mission mission;

    @OneToMany(mappedBy = "member")
    private List<Board> boardList = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<DiaryMember> diaryMembers = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<Diary> diaryList = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Like> likeList = new ArrayList<>();

    @Builder
    public Member(Role role, String email, String profileName, String refreshToken, String fcmToken) {
        this.role = role;
        this.email = email;
        this.profileName = profileName;
        this.refreshToken = refreshToken;
        this.fcmToken = fcmToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setProfileName(String profileName){
        this.profileName = profileName;
    }

    public void setMemberPhoto(String photoUrl){
        this.photoUrl = photoUrl;
    }

    public void setMemberRole(String provider){
       if(provider.equals("google")){
            this.role = Role.ROLE_GOOGLE_USER;
       }

       if(provider.equals("kakao")){
           this.role = Role.ROLE_KAKAO_USER;
       }
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
