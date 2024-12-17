package com.knu.fromnow.api.domain.diary.service;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.board.repository.BoardRepository;
import com.knu.fromnow.api.domain.diary.dto.request.AcceptDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.CreateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.ImmediateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.InviteToDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.RejectDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.UpdateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryCreateResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryDeleteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryImmeInviteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryInviteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryMenuResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryReadColResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryReadCompleteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryReadRowResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryRequestsReceivedDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiarySearchResponseDto;
import com.knu.fromnow.api.domain.member.repository.MemberCustomRepository;
import com.knu.fromnow.api.domain.tracking.entity.DateLatestPostTime;
import com.knu.fromnow.api.domain.tracking.entity.DateReadTracking;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import com.knu.fromnow.api.domain.tracking.repository.DateLatestPostTimeRepository;
import com.knu.fromnow.api.domain.tracking.repository.DateReadTrackingRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberCustomRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryRepository;
import com.knu.fromnow.api.domain.friend.repository.FriendCustomRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.domain.tracking.service.DateLatestPostTimeService;
import com.knu.fromnow.api.domain.tracking.service.DateReadTrackingService;
import com.knu.fromnow.api.global.error.custom.DateReadTrackingException;
import com.knu.fromnow.api.global.error.custom.DiaryException;
import com.knu.fromnow.api.global.error.custom.DiaryMemberException;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.DateReadTrackingErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.DiaryErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.DiaryMemberErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import com.knu.fromnow.api.global.firebase.service.FirebaseService;
import com.knu.fromnow.api.global.spec.api.ApiBasicResponse;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import com.knu.fromnow.api.global.spec.date.request.DateRequestDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryService {

    private static final Logger log = LoggerFactory.getLogger(DiaryService.class);
    private final MemberCustomRepository memberCustomRepository;
    private final DiaryMemberService diaryMemberService;
    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;
    private final DiaryMemberRepository diaryMemberRepository;
    private final DiaryMemberCustomRepository diaryMemberCustomRepository;
    private final FriendCustomRepository friendCustomRepository;
    private final DateReadTrackingRepository dateReadTrackingRepository;
    private final DateLatestPostTimeRepository dateLatestPostTimeRepository;
    private final DateLatestPostTimeService dateLatestPostTimeService;
    private final DateReadTrackingService dateReadTrackingService;
    private final FirebaseService firebaseService;
    private final BoardRepository boardRepository;

    public ApiDataResponse<DiaryCreateResponseDto> createDiary(CreateDiaryDto createDiaryDto, PrincipalDetails principalDetails) {
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Diary diary = Diary.builder()
                .title(createDiaryDto.getTitle())
                .owner(member)
                .build();
        diaryRepository.save(diary);

        LocalDateTime createdAt = diary.getCreatedAt();
        LocalDate today = createdAt.toLocalDate();

        log.info("now = {}", createdAt);
        log.info("today = {}", today);

        diaryMemberService.initMemberToDiary(diary, member, createdAt);
        dateReadTrackingService.initDateReadTracking(diary, member, today);
        dateLatestPostTimeService.initDateLatestPostTime(diary, today);

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

        List<Long> diaryIds = diaryMemberRepository.findDiaryIdsByMemberIdWithAcceptedInviteTrue(member.getId());
        List<Diary> diaryList = diaryRepository.findByIdIn(diaryIds);
        List<DiaryOverViewResponseDto> responseDtoList = diaryMemberCustomRepository.fetchDiaryOverviewDtosByDiaryMembers(diaryList, member);
        responseDtoList.sort(Comparator.comparing(DiaryOverViewResponseDto::getRecivedAt).reversed());

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
        dateReadTrackingRepository.deleteAllByDiaryId(diary.getId());
        dateLatestPostTimeRepository.deleteAllByDiaryId(diary.getId());

        return ApiDataResponse.<DiaryDeleteResponseDto>builder()
                .status(true)
                .code(200)
                .message("다이어리 삭제 성공")
                .data(DiaryDeleteResponseDto.builder()
                        .id(diary.getId())
                        .build())
                .build();
    }


    public ApiDataResponse<List<DiaryInviteResponseDto>> inviteToDiary(InviteToDiaryDto inviteToDiaryDto, PrincipalDetails principalDetails) {
        Diary diary = diaryRepository.findById(inviteToDiaryDto.getDiaryId())
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        Member owner = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        if (!diary.getOwner().equals(owner)) {
            throw new MemberException(MemberErrorCode.NO_OWNER_EXCEPTION);
        }

        List<Member> invitedMembers = memberRepository.findByProfileNameIn(inviteToDiaryDto.getProfileNames());
        List<DiaryMember> diaryMembers = diaryMemberRepository.findByDiary(diary);

        for (Member member : invitedMembers) {
            boolean alreadyInvited = diaryMembers.stream()
                    .anyMatch(diaryMember -> diaryMember.getMember().equals(member));

            if (alreadyInvited) {
                throw new MemberException(MemberErrorCode.ALREADY_INVITED_EXCEPTION); // Custom error code
            }
        }

        diaryMemberService.inviteMemberToDiary(diary, invitedMembers);

        List<DiaryInviteResponseDto> data = firebaseService.sendDiaryNotificationToInvitedMembers(owner, invitedMembers, diary);

        return ApiDataResponse.<List<DiaryInviteResponseDto>>builder()
                .status(true)
                .code(200)
                .message("모임 요청을 성공적으로 보냈습니다. 초대를 받은 멤버 데이터는 아래와 같습니다.")
                .data(data)
                .build();
    }

    public ApiDataResponse<DiaryOverViewResponseDto> acceptInvite(AcceptDiaryDto acceptDiaryDto, PrincipalDetails principalDetails) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

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
        diaryMember.updateRecievedAt(now);
        diaryMemberRepository.save(diaryMember);
        List<String> photoUrls = diaryMemberCustomRepository.fetchMemberPhotoUrlsByDiary(diary);

        // tracking 정보 추가하기
        createTrackingWhenAcceptedDiary(member, diary, today);

        return ApiDataResponse.<DiaryOverViewResponseDto>builder()
                .status(true)
                .code(200)
                .message("초대 수락 성공! 리스트에 추가할 다이어리 데이터는 아래와 같습니다.")
                .data(DiaryOverViewResponseDto.builder()
                        .id(diary.getId())
                        .title(diary.getTitle())
                        .photoUrls(photoUrls)
                        .createdAt(diary.getCreatedAt().toString())
                        .recivedAt(now.toString())
                        .build())
                .build();
    }

    public void createTrackingWhenAcceptedDiary(Member member, Diary diary, LocalDate today) {
        DateReadTracking dateReadTracking = DateReadTracking.builder()
                .diaryId(diary.getId())
                .memberId(member.getId())
                .isWrite(false)
                .date(today)
                .lastedMemberReadTime(today.atStartOfDay())
                .build();

        dateReadTrackingRepository.save(dateReadTracking);
    }

    public ApiDataResponse<List<DiaryMenuResponseDto>> getDiaryMenu(Long id, PrincipalDetails principalDetails) {
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
            if (findMember.getId().equals(owner.getId())) {
                isOwner = true;
            }

            boolean isFriend = false;
            if (friendsAmongSpecificMembers.contains(findMember.getId())) {
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


    public ApiDataResponse<List<DiaryReadRowResponseDto>> getRowScroll(Long id, int year, int month, PrincipalDetails principalDetails) {
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        // 다이어리의 멤버인지 확인
        if (!diaryMemberRepository.existsByDiaryAndMember(diary, member)) {
            throw new DiaryMemberException(DiaryMemberErrorCode.NO_EXIST_DIARY_MEMBER_EXCEPTION);
        }

        LocalDate startDate = LocalDate.of(year, month, 1); // 해당 월의 1일
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth()); // 해당 월의 마지막 날

        List<DateReadTracking> dateReadTrackingList = dateReadTrackingRepository.findByMemberIdAndDiaryIdAndDateBetweenOrderByDateAsc(member.getId(), diary.getId(), startDate, endDate);
        List<DateLatestPostTime> dateLatestPostTimeList = dateLatestPostTimeRepository.findByDiaryIdAndDateBetween(diary.getId(), startDate, endDate);

        List<DiaryReadRowResponseDto> data = new ArrayList<>();

        for (int i = 0; i < dateLatestPostTimeList.size(); i++) {

            DateLatestPostTime dateLatestPostTime = dateLatestPostTimeList.get(i);
            DateReadTracking dateReadTracking = dateReadTrackingList.get(i);

            LocalDateTime startOfDay = dateLatestPostTime.getDate().atStartOfDay();

            LocalDateTime lastedPostTime = dateLatestPostTime.getLatestPostTime();
            LocalDateTime lastedMemberReadTime = dateReadTracking.getLastedMemberReadTime();
            // 마지막으로 글이 써진 시간이, 내가 읽은 시점보다 더 지났을때
            boolean isNew = lastedPostTime.isAfter(lastedMemberReadTime);
            // 마지막으로 글이 써진 시간이, 자정 00:00:00이 아니면
            boolean hasPosts = !lastedPostTime.equals(startOfDay);
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

        if (!diaryMemberRepository.existsByDiaryAndMember(diary, member)) {
            throw new DiaryMemberException(DiaryMemberErrorCode.NO_EXIST_DIARY_MEMBER_EXCEPTION);
        }

        LocalDate date = LocalDate.of(dateRequestDto.getYear(), dateRequestDto.getMonth(), dateRequestDto.getDay());

        DateReadTracking dateReadTracking = dateReadTrackingRepository.findByMemberIdAndDiaryIdAndDate(member.getId(), diary.getId(), date)
                .orElseThrow(() -> new DateReadTrackingException(DateReadTrackingErrorCode.NO_DATE_READ_TRACKING_EXIST_EXCEPTION));

        dateReadTracking.updateLastedMemberReadTime(LocalDateTime.now());
        dateReadTrackingRepository.save(dateReadTracking);

        return ApiDataResponse.<DiaryReadCompleteResponseDto>builder()
                .status(true)
                .code(200)
                .message("해당 날짜 읽음 처리 성공!")
                .data(DiaryReadCompleteResponseDto
                        .builder()
                        .diaryId(diary.getId())
                        .date(date.toString())
                        .isRead(true)
                        .build())
                .build();
    }

    public ApiDataResponse<List<DiarySearchResponseDto>> searchMember(Long diaryId, String profileName) {
        List<Long> alreadyInDiaryMembers = diaryMemberRepository.findMemberIdsByDiaryIdAndAcceptedInviteTrue(diaryId);
        List<Long> alreadyInvitedMembers = diaryMemberRepository.findMemberIdsByDiaryIdAndAcceptedInviteFalse(diaryId);
        if (alreadyInDiaryMembers.isEmpty()) {
            throw new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION);
        }
        // 현재 다이어리에 A, B, C, AA, AAA 세명이 있음

        // A를 검색하면 A, AA, AAA를 가져올 것.
        List<Member> members = memberCustomRepository.findMembersByProfileNameContainingIgnoreCase(profileName);

        List<DiarySearchResponseDto> list = new ArrayList<>();

        for (Member member : members) {
            if (!alreadyInvitedMembers.contains(member.getId())) {
                boolean isTeam = alreadyInDiaryMembers.contains(member.getId());

                list.add(DiarySearchResponseDto.builder()
                        .memberId(member.getId())
                        .profilePhotoUrl(member.getPhotoUrl())
                        .profileName(member.getProfileName())
                        .isTeam(isTeam)
                        .build());
            }
        }

        return ApiDataResponse.<List<DiarySearchResponseDto>>builder()
                .status(true)
                .code(200)
                .message("다이어리에서 친구 검색 하는 Api 응답 성공")
                .data(list)
                .build();
    }

    public ApiDataResponse<List<DiaryReadColResponseDto>> getColScroll(
            Long diaryId, int year, int month, int num, PrincipalDetails principalDetails) {

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        if (!diaryMemberRepository.existsByDiaryAndMember(diary, member)) {
            throw new DiaryMemberException(DiaryMemberErrorCode.NO_EXIST_DIARY_MEMBER_EXCEPTION);
        }

        // 조회 기간의 시작 날짜와 종료 날짜 계산
        LocalDate endDate = LocalDate.of(year, month, 1).withDayOfMonth(1);
        LocalDate startDate = endDate.minusMonths(num - 1).withDayOfMonth(1);

        // 해당 기간 동안의 모든 게시물을 가져옵니다.
        List<Board> boards = boardRepository.findByDiaryIdAndCreatedAtBetween(
                diaryId,
                startDate.atStartOfDay(),
                endDate.withDayOfMonth(endDate.lengthOfMonth()).atTime(23, 59, 59)
        );

        // 게시물을 날짜별로 그룹화합니다.
        Map<LocalDate, List<Board>> boardsByDate = boards.stream()
                .collect(Collectors.groupingBy(board -> board.getCreatedAt().toLocalDate()));

        // 기간 내의 모든 날짜 리스트를 가져옵니다.
        List<LocalDate> dateList = new ArrayList<>(boardsByDate.keySet());

        // 회원의 해당 기간 내 작성 여부 데이터를 한 번에 가져옵니다.
        List<DateReadTracking> trackingList = dateReadTrackingRepository.findByMemberIdAndDiaryIdAndDateIn(
                member.getId(), diaryId, dateList);

        // DateReadTracking 데이터를 맵으로 변환합니다.
        Map<LocalDate, Boolean> isWriteMap = trackingList.stream()
                .collect(Collectors.toMap(
                        DateReadTracking::getDate,
                        DateReadTracking::isWrite
                ));

        // 날짜별로 DiaryReadColResponseDto 생성
        List<DiaryReadColResponseDto> diaryReadColResponseDtos = boardsByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();

                    // 회원의 작성 여부를 맵에서 조회, 없으면 false
                    boolean hasWritten = isWriteMap.getOrDefault(date, false);
                    List<String> photoUrls = entry.getValue().stream()
                            .map(board -> board.getBoardPhoto().getPhotoUrl())
                            .toList();

                    return DiaryReadColResponseDto.builder()
                            .date(date.toString())
                            .isBlur(!hasWritten)
                            .photoUrls(photoUrls)
                            .build();
                })
                .collect(Collectors.toList());

        return ApiDataResponse.<List<DiaryReadColResponseDto>>builder()
                .status(true)
                .code(200)
                .data(diaryReadColResponseDtos)
                .build();
    }

    public ApiBasicResponse rejectInvite(RejectDiaryDto rejectDiaryDto, PrincipalDetails principalDetails) {
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Diary diary = diaryRepository.findById(rejectDiaryDto.getRejectDiaryId())
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        DiaryMember diaryMember = diaryMemberRepository.findByDiaryAndMemberAndAcceptedInviteFalse(diary, member)
                .orElseThrow(() -> new DiaryMemberException(DiaryMemberErrorCode.NO_EXIST_DIARY_MEMBER_EXCEPTION));

        diaryMemberRepository.delete(diaryMember);

        return ApiBasicResponse.builder()
                .status(true)
                .code(200)
                .message("다이어리 초대 거절 성공")
                .build();
    }

    public ApiDataResponse<DiaryImmeInviteResponseDto> immediateInviteToDiary(ImmediateDiaryDto immediateDiaryDto, PrincipalDetails principalDetails) {
        Diary diary = diaryRepository.findById(immediateDiaryDto.getDiaryId())
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        Member member = memberRepository.findByProfileName(immediateDiaryDto.getProfileName())
                .orElseThrow(() -> new MemberException(MemberErrorCode.NO_EXIST_PROFILE_NAME_MEMBER_EXCEPTION));

        List<DiaryMember> diaryMembers = diaryMemberRepository.findByDiary(diary);

        boolean alreadyInvited = diaryMembers.stream()
                .anyMatch(diaryMember -> diaryMember.getMember().equals(member));

        if (alreadyInvited) {
            throw new MemberException(MemberErrorCode.ALREADY_INVITED_EXCEPTION); // Custom error code
        }

        diaryMemberService.immediateInviteMemberToDiary(diary, member);
        DiaryImmeInviteResponseDto data = DiaryImmeInviteResponseDto.makeFrom(member);

        return ApiDataResponse.<DiaryImmeInviteResponseDto>builder()
                .status(true)
                .code(200)
                .message("모임 요청을 성공적으로 보냈습니다. 다이어리에 추가 된 멤버 데이터는 아래와 같습니다.")
                .data(data)
                .build();
    }
}
