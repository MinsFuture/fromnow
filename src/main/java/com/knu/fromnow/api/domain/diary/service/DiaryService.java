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
import com.knu.fromnow.api.domain.diary.dto.response.DiaryReadRowResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryRequestsReceivedDto;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberCustomRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryRepository;
import com.knu.fromnow.api.domain.friend.repository.FriendCustomRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.domain.read.entity.BoardRead;
import com.knu.fromnow.api.domain.read.repository.BoardReadRepository;
import com.knu.fromnow.api.global.error.custom.BoardException;
import com.knu.fromnow.api.global.error.custom.DiaryException;
import com.knu.fromnow.api.global.error.custom.DiaryMemberException;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.BoardErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.DiaryErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.DiaryMemberErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import com.knu.fromnow.api.global.spec.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final BoardReadRepository boardReadRepository;

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


    public ApiDataResponse<List<DiaryReadRowResponseDto>> getRowScroll(Long id, Long year, Long month, PrincipalDetails principalDetails) {
        Diary diary= diaryRepository.findById(id)
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));


        LocalDate startDate = LocalDate.of(year.intValue(), month.intValue(), 1); // 해당 월의 1일
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth()); // 해당 월의 마지막 날

        // 시간 범위 설정 (해당 월의 첫째 날 00:00부터 마지막 날 23:59까지)
        LocalDateTime startDateTime = startDate.atStartOfDay(); // 해당 월의 1일 00:00:00
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay(); // 다음 달 1일 00:00:00 (exclusive)

        // 해당 기간에 속하는 모든 Board를 조회
        List<Board> boards = boardRepository.findByDiaryIdAndCreatedAtBetween(diary.getId(), startDateTime, endDateTime);
        List<BoardRead> boardReads = boardReadRepository.findByMemberAndBoardIn(member, boards); // 해당 사용자가 읽은 게시글

        // 각 날짜에 글이 있는지 여부와 읽었는지 여부를 저장할 맵 (key: 날짜, value: 읽기 상태)
        Map<LocalDate, DiaryReadRowResponseDto> diaryReadRowResponseDtoMap = new HashMap<>();

        // 게시글 리스트를 날짜별로 분류
        Map<LocalDate, List<Board>> boardsByDate = boards.stream()
                .collect(Collectors.groupingBy(board -> board.getCreatedAt().toLocalDate()));

        // 사용자가 읽은 게시글 ID 리스트 추출
        Set<Long> readBoardIds = boardReads.stream()
                .map(boardRead -> boardRead.getBoard().getId())
                .collect(Collectors.toSet());

        List<DiaryReadRowResponseDto> list = new ArrayList<>();

        // 해당 달의 각 날짜를 순회하며 게시글이 있는지 확인하고, 사용자가 읽었는지도 확인
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // 해당 날짜의 게시글 리스트 가져오기
            List<Board> boardsForDate = boardsByDate.getOrDefault(date, Collections.emptyList());

            // 해당 날짜에 게시글이 존재하는지 확인
            boolean hasBoard = !boardsForDate.isEmpty();

            // 해당 날짜의 게시글 중 하나라도 사용자가 읽었는지 확인
            boolean hasRead = boardsForDate.stream()
                    .anyMatch(board -> readBoardIds.contains(board.getId()));

            // 해당 날짜에 대한 상태를 저장 (글 존재 여부와 읽기 여부)
            list.add(DiaryReadRowResponseDto.builder()
                            .hasPosts(hasBoard)
                            .isNew(!hasRead)
                            .year(date.getYear())
                            .month(date.getMonthValue())
                            .day(date.getDayOfMonth())
                    .build());
        }

        return ApiDataResponse.<List<DiaryReadRowResponseDto>>builder()
                .status(true)
                .code(200)
                .message(month + "월에 해당하는 날짜별 게시글 확인 및 읽기 상태 조회 성공!")
                .data(list)
                .build();
    }
}
