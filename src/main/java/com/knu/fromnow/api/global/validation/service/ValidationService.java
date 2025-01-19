package com.knu.fromnow.api.global.validation.service;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.board.repository.BoardRepository;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryRepository;
import com.knu.fromnow.api.domain.like.entity.Like;
import com.knu.fromnow.api.domain.like.repository.LikeRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.domain.tracking.entity.DateLatestPostTime;
import com.knu.fromnow.api.domain.tracking.entity.DateReadTracking;
import com.knu.fromnow.api.domain.tracking.repository.DateLatestPostTimeRepository;
import com.knu.fromnow.api.domain.tracking.repository.DateReadTrackingRepository;
import com.knu.fromnow.api.global.error.custom.BoardException;
import com.knu.fromnow.api.global.error.custom.DateLatestPostTimeException;
import com.knu.fromnow.api.global.error.custom.DateReadTrackingException;
import com.knu.fromnow.api.global.error.custom.DiaryException;
import com.knu.fromnow.api.global.error.custom.DiaryMemberException;
import com.knu.fromnow.api.global.error.custom.LikeException;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.BoardErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.DateLatestPostTimeErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.DateReadTrackingErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.DiaryErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.DiaryMemberErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.LikeErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ValidationService {
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final DiaryRepository diaryRepository;
    private final LikeRepository likeRepository;
    private final DateReadTrackingRepository dateReadTrackingRepository;
    private final DateLatestPostTimeRepository dateLatestPostTimeRepository;
    private final DiaryMemberRepository diaryMemberRepository;


    public void checkProfileNameUniqueness(String profileName){
        if (memberRepository.existsByProfileName(profileName)){
            throw new MemberException(MemberErrorCode.CONFLICT_PROFILE_NAME_MEMBER_EXCEPTION);
        }
    }
    public Member validateMemberByEmail(String email){
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));
    }

    public Member validateMemberByProfileName(String profileName){
        return memberRepository.findByProfileName(profileName)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NO_EXIST_PROFILE_NAME_MEMBER_EXCEPTION));
    }

    public Board validateBoardById(Long id){
       return boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(BoardErrorCode.NO_EXIST_BOARD_EXCEPTION));
    }

    public Diary validateDiaryById(Long id){
        return diaryRepository.findById(id)
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_ID_EXCEPTION));
    }

    public DiaryMember validateMemberAndDiary(Member member, Diary diary){
        return diaryMemberRepository.findByDiaryAndMember(diary, member)
                .orElseThrow(() -> new DiaryMemberException(DiaryMemberErrorCode.NO_EXIST_DIARY_MEMBER_EXCEPTION));
    }

    public Like validateLikeByMemberAndBoard(Member member, Board board){
        return likeRepository.findByMemberAndBoard(member, board)
                .orElseThrow(() -> new LikeException(LikeErrorCode.NEVER_CLICK_THE_LIKE_BUTTON_EXCEPTION));
    }

    public DateReadTracking validateDateReadTracking(Long myId, Long diaryId, LocalDate currentDate){
        return dateReadTrackingRepository.findByMemberIdAndDiaryIdAndDate(myId, diaryId, currentDate)
                .orElseThrow(() -> new DateReadTrackingException(DateReadTrackingErrorCode.NO_DATE_READ_TRACKING_EXIST_EXCEPTION));
    }

    public DateLatestPostTime validateDateLatestPostTime(Long diaryId, LocalDate currentDate){
        return dateLatestPostTimeRepository.findByDiaryIdAndDate(diaryId, currentDate)
                .orElseThrow(() -> new DateLatestPostTimeException(DateLatestPostTimeErrorCode.NO_DATE_LATEST_POST_TIME_EXCEPTION));
    }
}
