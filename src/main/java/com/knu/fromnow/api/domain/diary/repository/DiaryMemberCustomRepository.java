package com.knu.fromnow.api.domain.diary.repository;

import com.knu.fromnow.api.domain.diary.dto.response.DiaryOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface DiaryMemberCustomRepository {
    List<DiaryOverViewResponseDto> fetchDiaryOverviewDtosByDiaryMembers(List<Diary> diaryList, Member member);
    List<Long> getUnacceptedDiaryIdsByMember(Member member);
    List<String> fetchMemberPhotoUrlsByDiary(Diary diary);
    Map<Member, LocalDateTime> findRecievedAtByDiaryAndMembers(Diary diary, List<Member> members);
    List<Member> findMembersByDiaryIdExceptMe(Diary diary, Member member);

}
