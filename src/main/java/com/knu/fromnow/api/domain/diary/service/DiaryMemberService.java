package com.knu.fromnow.api.domain.diary.service;

import com.knu.fromnow.api.domain.diary.repository.DiaryMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryMemberService {
    private final DiaryMemberRepository diaryMemberRepository;
}
