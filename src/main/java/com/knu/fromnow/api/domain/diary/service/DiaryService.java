package com.knu.fromnow.api.domain.diary.service;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.board.repository.BoardRepository;
import com.knu.fromnow.api.domain.diary.dto.request.AcceptDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.CreateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.InviteToDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.UpdateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryCreateResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryDeleteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryInviteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryMenuResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryReadCompleteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryReadRowResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryRequestsReceivedDto;
import com.knu.fromnow.api.domain.diary.entity.DateReadTracking;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import com.knu.fromnow.api.domain.diary.repository.DateReadTrackingRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberCustomRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryRepository;
import com.knu.fromnow.api.domain.friend.repository.FriendCustomRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.global.error.custom.DateReadTrackingException;
import com.knu.fromnow.api.global.error.custom.DiaryException;
import com.knu.fromnow.api.global.error.custom.DiaryMemberException;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.DateReadTrackingErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.DiaryErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.DiaryMemberErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import com.knu.fromnow.api.global.spec.ApiDataResponse;
import com.knu.fromnow.api.global.spec.date.request.DateRequestDto;
import com.knu.fromnow.api.global.spec.date.request.YearMonthRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryService {

    private final BoardRepository boardRepository;
    private final DiaryMemberService diaryMemberService;
    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;
    private final DiaryMemberRepository diaryMemberRepository;
    private final DiaryMemberCustomRepository diaryMemberCustomRepository;
    private final FriendCustomRepository friendCustomRepository;
    private final DateReadTrackingRepository dateReadTrackingRepository;

    public ApiDataResponse<DiaryCreateResponseDto> createDiary(CreateDiaryDto createDiaryDto, PrincipalDetails principalDetails) {

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Diary diary = Diary.builder()
                .title(createDiaryDto.getTitle())
                .owner(member)
                .build();

        diaryRepository.save(diary);

        DiaryMember diaryMember = DiaryMember.builder()
                .diary(diary)
                .member(member)
                .acceptedInvite(true)
                .build();

        member.getDiaryMembers().add(diaryMember);
        diary.getDiaryMembers().add(diaryMember);

        diaryMemberRepository.save(diaryMember);

        List<String> photoUrls = diaryMemberCustomRepository.fetchMemberPhotoUrlsByDiary(diary);

        return ApiDataResponse.<DiaryCreateResponseDto>builder()
                .status(true)
                .code(200)
                .message("다이어리 생성 성공")
                .data(DiaryCreateResponseDto.fromDiary(diary))
                .build();
    }

    public ApiDataResponse<List<DiaryOverViewResponseDto>> getDiaryOverView(PrincipalDetails principalDetails) {
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        List<Long> diaryIds = diaryMemberRepository.findDiaryIdsByMemberId(member.getId());
        List<Diary> diaryList = diaryRepository.findByIdIn(diaryIds);
        List<DiaryOverViewResponseDto> responseDtoList = diaryMemberCustomRepository.fetchDiaryOverviewDtosByDiaryMembers(diaryList);

        return ApiDataResponse.<List<DiaryOverViewResponseDto>>builder()
                .status(true)
                .code(200)
                .message("다이어리 리스트 반환 성공!")
                .data(responseDtoList)
                .build();
    }


    public ApiDataResponse<DiaryRequestsReceivedDto> updateDiaryTitle(UpdateDiaryDto updateDiaryDto, PrincipalDetails principalDetails, Long diaryId) {
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        if (!diary.getOwner().equals(member)) {
            throw new MemberException(MemberErrorCode.NO_OWNER_EXCEPTION);
        }

        diary.updateDiaryTitle(updateDiaryDto);
        diaryRepository.save(diary);

        return ApiDataResponse.<DiaryRequestsReceivedDto>builder()
                .status(true)
                .code(200)
                .message("다이어리 타이틀 수정")
                .data(DiaryRequestsReceivedDto.builder()
                        .diaryId(diary.getId())
                        .diaryTitle(diary.getTitle())
                        .build())
                .build();
    }

    public ApiDataResponse<DiaryDeleteResponseDto> deleteDiary(PrincipalDetails principalDetails, Long diaryId) {
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        if (!diary.getOwner().equals(member)) {
            throw new MemberException(MemberErrorCode.NO_OWNER_EXCEPTION);
        }

        diaryRepository.delete(diary);

        return ApiDataResponse.<DiaryDeleteResponseDto>builder()
                .status(true)
                .code(200)
                .message("다이어리 삭제 성공")
                .data(DiaryDeleteResponseDto.builder()
                        .id(diary.getId())
                        .build())
                .build();
    }


    public ApiDataResponse<DiaryInviteResponseDto> inviteToDiary(InviteToDiaryDto inviteToDiaryDto, PrincipalDetails principalDetails) {
        Diary diary = diaryRepository.findById(inviteToDiaryDto.getDiaryId())
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        Member owner = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        if (!diary.getOwner().equals(owner)) {
            throw new MemberException(MemberErrorCode.NO_OWNER_EXCEPTION);
        }

        Member invitedMember = memberRepository.findByProfileName(inviteToDiaryDto.getProfileName())
                .orElseThrow(() -> new MemberException(MemberErrorCode.NO_EXIST_PROFILE_NAME_MEMBER_EXCEPTION));

        diaryMemberService.inviteMemberToDiary(diary, invitedMember);

        return ApiDataResponse.<DiaryInviteResponseDto>builder()
                .status(true)
                .code(200)
                .message("모임 요청을 성공적으로 보냈습니다. 초대를 받은 멤버 데이터는 아래와 같습니다.")
                .data(DiaryInviteResponseDto.makeFrom(invitedMember))
                .build();
    }

    public ApiDataResponse<DiaryOverViewResponseDto> acceptInvite(AcceptDiaryDto acceptDiaryDto, PrincipalDetails principalDetails) {
        Diary diary = diaryRepository.findById(acceptDiaryDto.getDiaryId())
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        DiaryMember diaryMember = diaryMemberRepository.findByDiaryAndMember(diary, member)
                .orElseThrow(() -> new DiaryMemberException(DiaryMemberErrorCode.NO_EXIST_DIARY_MEMBER_EXCEPTION));

        if (diaryMember.isAcceptedInvite()) {
            throw new DiaryMemberException(DiaryMemberErrorCode.ALREADY_ACCEPTED_INVITATION_EXCEPTION);
        }

        diaryMember.acceptInvitation();
        diaryMemberRepository.save(diaryMember);

        List<String> photoUrls = diaryMemberCustomRepository.fetchMemberPhotoUrlsByDiary(diary);

        return ApiDataResponse.<DiaryOverViewResponseDto>builder()
                .status(true)
                .code(200)
                .message("초대 수락 성공! 리스트에 추가할 다이어리 데이터는 아래와 같습니다.")
                .data(DiaryOverViewResponseDto.builder()
                        .id(diary.getId())
                        .title(diary.getTitle())
                        .photoUrls(photoUrls)
                        .build())
                .build();
    }

    public ApiDataResponse<List<DiaryMenuResponseDto>> getDiaryMenu(Long id, PrincipalDetails principalDetails){
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Member owner = diary.getOwner();

        List<DiaryMember> diaryMembers = diary.getDiaryMembers();
        List<Long> diaryMemberIds = diaryMembers.stream().map(diaryMember -> diaryMember.getMember().getId()).toList();

        List<Long> friendsAmongSpecificMembers = friendCustomRepository.findFriendsAmongSpecificMembers(member.getId(), diaryMemberIds);

        List<DiaryMenuResponseDto> responseDtoList = new ArrayList<>();

        for (DiaryMember diaryMember : diaryMembers) {
            Member findMember = diaryMember.getMember();

            boolean isOwner = false;
            if(findMember.getId().equals(owner.getId())){
                isOwner = true;
            }

            boolean isFriend = false;
            if(friendsAmongSpecificMembers.contains(findMember.getId())){
                isFriend = true;
            }

            responseDtoList.add(DiaryMenuResponseDto.builder()
                    .isOwner(isOwner)
                    .memberId(findMember.getId())
                    .photoUrl(findMember.getPhotoUrl())
                    .profileName(findMember.getProfileName())
                    .isFriend(isFriend)
                    .build());
        }

        return ApiDataResponse.<List<DiaryMenuResponseDto>>builder()
                .status(true)
                .code(200)
                .message("다이어리 메뉴 정보 불러오기")
                .data(responseDtoList)
                .build();
    }


    public ApiDataResponse<List<DiaryReadRowResponseDto>> getRowScroll(Long id, YearMonthRequestDto yearMonthRequestDto, PrincipalDetails principalDetails) {
        Diary diary= diaryRepository.findById(id)
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        LocalDate startDate = LocalDate.of(yearMonthRequestDto.getYear(), yearMonthRequestDto.getMonth(), 1); // 해당 월의 1일
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth()); // 해당 월의 마지막 날

        List<DateReadTracking> dateReadTrackingList = dateReadTrackingRepository.findByMemberIdAndDiaryIdAndDateBetweenOrderByDateAsc(diary.getId(), member.getId(), startDate, endDate);
        List<DiaryReadRowResponseDto> data = new ArrayList<>();

        for (DateReadTracking dateReadTracking : dateReadTrackingList) {
            LocalDateTime lastedPostTime = dateReadTracking.getLastedPostTime();
            LocalDateTime lastedMemberReadTime = dateReadTracking.getLastedMemberReadTime();
            // 글이 있고, 읽은 적이 없거나 , 읽은 후에 새로운 글이 올라 온 경우
            boolean isNew = (lastedPostTime != null || lastedMemberReadTime == null || lastedPostTime.isAfter(lastedMemberReadTime));
            boolean hasPosts = dateReadTracking.getLastedPostTime() == null;
            LocalDate date = dateReadTracking.getDate();

            data.add(DiaryReadRowResponseDto.builder()
                            .isNew(isNew)
                            .hasPosts(hasPosts)
                            .date(date.toString())
                    .build());
        }

        return ApiDataResponse.<List<DiaryReadRowResponseDto>>builder()
                .status(true)
                .code(200)
                .message("가로 스크롤 Api 불러오기 성공")
                .data(data)
                .build();
    }

    public ApiDataResponse<DiaryReadCompleteResponseDto> readAllPostsByDate(Long diaryId, DateRequestDto dateRequestDto, PrincipalDetails principalDetails) {

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        LocalDate date = LocalDate.of(dateRequestDto.getYear(), dateRequestDto.getMonth(), dateRequestDto.getDay());

        DateReadTracking dateReadTracking = dateReadTrackingRepository.findByMemberIdAndDiaryIdAndDate(member.getId(), diary.getId(), date)
                .orElseThrow(() -> new DateReadTrackingException(DateReadTrackingErrorCode.NO_DATE_READ_TRACKING_EXIST_EXCEPTION));

        dateReadTracking.updateLastedMemberReadTime(LocalDateTime.now());
        dateReadTrackingRepository.save(dateReadTracking);

        return ApiDataResponse.<DiaryReadCompleteResponseDto>builder()
                .status(true)
                .code(200)
                .message("해당 날짜 읽음 처리 성공!")
                .data( DiaryReadCompleteResponseDto
                        .builder()
                        .diaryId(diary.getId())
                        .date(makeDateToString(date))
                        .isRead(true)
                        .build())
                .build();
    }

    public static String makeDateToString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }
}
