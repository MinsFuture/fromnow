package com.knu.fromnow.api.domain.diary.repository;

import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.member.entity.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

public interface DiaryMemberCustomRepository {
    List<Long> getUnacceptedDiaryIdsByMember(Member member);
    List<Member> getMemberListsByInDiary(Diary diary);
}
