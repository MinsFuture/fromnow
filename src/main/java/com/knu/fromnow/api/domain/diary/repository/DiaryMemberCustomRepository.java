package com.knu.fromnow.api.domain.diary.repository;

import com.knu.fromnow.api.domain.diary.dto.response.DiaryOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.member.entity.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

public interface DiaryMemberCustomRepository {

    List<DiaryOverViewResponseDto> fetchDiaryOverviewDtosByDiaryMembers(List<Diary> diaryList);
    List<Long> getUnacceptedDiaryIdsByMember(Member member);
    List<String> fetchMemberPhotoUrlsByDiary(Diary diary);
}
