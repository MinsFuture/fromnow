package com.knu.fromnow.api.domain.diary.service;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.board.repository.BoardRepository;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryMenuResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryReadColResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryReadCompleteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryReadRowResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiarySearchResponseDto;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberRepository;
import com.knu.fromnow.api.domain.friend.repository.FriendCustomRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberCustomRepository;
import com.knu.fromnow.api.domain.tracking.entity.DateLatestPostTime;
import com.knu.fromnow.api.domain.tracking.entity.DateReadTracking;
import com.knu.fromnow.api.domain.tracking.repository.DateLatestPostTimeRepository;
import com.knu.fromnow.api.domain.tracking.repository.DateReadTrackingRepository;
import com.knu.fromnow.api.global.error.custom.DiaryException;
import com.knu.fromnow.api.global.error.custom.DiaryMemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.DiaryErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.DiaryMemberErrorCode;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import com.knu.fromnow.api.global.spec.date.request.DateRequestDto;
import com.knu.fromnow.api.global.validation.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryManagementService {
    private final MemberCustomRepository memberCustomRepository;
    private final DiaryMemberRepository diaryMemberRepository;
    private final FriendCustomRepository friendCustomRepository;
    private final DateReadTrackingRepository dateReadTrackingRepository;
    private final DateLatestPostTimeRepository dateLatestPostTimeRepository;
    private final BoardRepository boardRepository;
    private final ValidationService validationService;

    public ApiDataResponse<List<DiaryMenuResponseDto>> getDiaryMenu(Long diaryId, PrincipalDetails principalDetails) {
        Member member = validationService.validateMemberByEmail(principalDetails.getEmail());
        Diary diary = validationService.validateDiaryById(diaryId);
        Member owner = diary.getOwner();

        List<DiaryMember> diaryMembers = diary.getDiaryMembers();
        List<Long> diaryMemberIds = diaryMembers.stream().map(diaryMember -> diaryMember.getMember().getId()).toList();
        List<Long> friendsAmongSpecificMembers = friendCustomRepository.findFriendsAmongSpecificMembers(member.getId(), diaryMemberIds);

        List<DiaryMenuResponseDto> responseDtoList = new ArrayList<>();
        for (DiaryMember diaryMember : diaryMembers) {
            Member findMember = diaryMember.getMember();
            boolean isOwner = findMember.getId().equals(owner.getId());
            boolean isFriend = friendsAmongSpecificMembers.contains(findMember.getId());
            responseDtoList.add(DiaryMenuResponseDto.of(isOwner, isFriend, findMember));
        }

        return ApiDataResponse.successResponse("다이어리 메뉴 정보 불러오기", responseDtoList);
    }


    public ApiDataResponse<List<DiaryReadRowResponseDto>> getRowScroll(Long diaryId, int year, int month, PrincipalDetails principalDetails) {
        Member member = validationService.validateMemberByEmail(principalDetails.getEmail());
        Diary diary = validationService.validateDiaryById(diaryId);

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

            data.add(DiaryReadRowResponseDto.of(isNew, hasPosts, date));
        }

        return ApiDataResponse.successResponse("가로 스크롤 Api 불러오기 성공", data);
    }

    public ApiDataResponse<DiaryReadCompleteResponseDto> readAllPostsByDate(Long diaryId, DateRequestDto dateRequestDto, PrincipalDetails principalDetails) {
        Member member = validationService.validateMemberByEmail(principalDetails.getEmail());
        Diary diary = validationService.validateDiaryById(diaryId);

        if (!diaryMemberRepository.existsByDiaryAndMember(diary, member)) {
            throw new DiaryMemberException(DiaryMemberErrorCode.NO_EXIST_DIARY_MEMBER_EXCEPTION);
        }

        LocalDate date = LocalDate.of(dateRequestDto.getYear(), dateRequestDto.getMonth(), dateRequestDto.getDay());
        DateReadTracking dateReadTracking = validationService.validateDateReadTracking(member.getId(), diary.getId(), date);
        dateReadTracking.updateLastedMemberReadTime(LocalDateTime.now());
        dateReadTrackingRepository.save(dateReadTracking);

        return ApiDataResponse.successResponse("해당 날짜 읽음 처리 성공!", DiaryReadCompleteResponseDto.of(diary.getId(), date));
    }

    public ApiDataResponse<List<DiarySearchResponseDto>> searchMember(Long diaryId, String profileName) {
        List<Long> alreadyInDiaryMembers = diaryMemberRepository.findMemberIdsByDiaryIdAndAcceptedInviteTrue(diaryId);
        List<Long> alreadyInvitedMembers = diaryMemberRepository.findMemberIdsByDiaryIdAndAcceptedInviteFalse(diaryId);
        if (alreadyInDiaryMembers.isEmpty()) {
            throw new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_ID_EXCEPTION);
        }
        // 현재 다이어리에 A, B, C, AA, AAA 세명이 있음

        // A를 검색하면 A, AA, AAA를 가져올 것.
        List<Member> members = memberCustomRepository.findMembersByProfileNameContainingIgnoreCase(profileName);

        List<DiarySearchResponseDto> list = new ArrayList<>();

        for (Member member : members) {
            if (!alreadyInvitedMembers.contains(member.getId())) {
                list.add(DiarySearchResponseDto.of(member, alreadyInDiaryMembers.contains(member.getId())));
            }
        }

        return ApiDataResponse.successResponse("다이어리에서 친구 검색 하는 Api 응답 성공", list);
    }

    public ApiDataResponse<List<DiaryReadColResponseDto>> getColScroll(Long diaryId, int year, int month, int num, PrincipalDetails principalDetails) {
        Member member = validationService.validateMemberByEmail(principalDetails.getEmail());
        Diary diary = validationService.validateDiaryById(diaryId);

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


                    return DiaryReadColResponseDto.of(date, hasWritten, photoUrls);
                })
                .collect(Collectors.toList());

        return ApiDataResponse.successResponse("세로 스크롤 불러오기 성공!", diaryReadColResponseDtos);
    }
}
