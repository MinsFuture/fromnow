package com.knu.fromnow.api.domain.board.repository;

import com.knu.fromnow.api.domain.board.dto.response.BoardOverViewResponseDto;
import com.knu.fromnow.api.domain.board.entity.QBoard;
import com.knu.fromnow.api.domain.like.entity.QLike;
import com.knu.fromnow.api.domain.member.entity.QMember;
import com.knu.fromnow.api.domain.photo.entity.QBoardPhoto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardCustomRepositoryImpl implements BoardCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<BoardOverViewResponseDto> findByDiaryIdAndCreatedAtBetween(Long myId, Long diaryId, LocalDateTime startDate, LocalDateTime endDate) {
        QBoard board = QBoard.board;
        QMember member = QMember.member;
        QBoardPhoto boardPhoto = QBoardPhoto.boardPhoto;
        QLike like = QLike.like;

        return jpaQueryFactory.select(Projections.constructor(BoardOverViewResponseDto.class,
                        board.id.as("boardId"),
                        board.createdAt.stringValue().as("createdDate"),
                        member.photoUrl.as("profilePhotoUrl"),
                        member.profileName.as("profileName"),
                        boardPhoto.photoUrl.as("contentPhotoUrl"),
                        board.content.as("content"),
                        board.like.as("likes"),
                        // 사용자가 해당 게시물을 좋아요 했는지 여부를 서브쿼리로 확인합니다.
                        JPAExpressions.selectOne()
                                .from(like)
                                .where(like.board.eq(board)
                                        .and(like.member.id.eq(myId))
                                        .and(like.isLiked.isTrue()))
                                .exists()
                                .as("isLiked")
                ))
                .from(board)
                .join(board.member, member) // xxToOne 관계 페치 조인
                .join(board.boardPhoto, boardPhoto) // xxToOne 관계 페치 조인
                .where(board.diary.id.eq(diaryId)
                        .and(board.createdAt.between(startDate, endDate)))
                .orderBy(board.createdAt.desc())
                .fetch();
    }

    @Override
    public List<BoardOverViewResponseDto> findBoardsLikedByMember(Long myId) {
        QBoard board = QBoard.board;
        QMember member = QMember.member;
        QBoardPhoto boardPhoto = QBoardPhoto.boardPhoto;
        QLike like = QLike.like;

        return jpaQueryFactory.select(Projections.constructor(BoardOverViewResponseDto.class,
                        board.id.as("boardId"),
                        board.createdAt.stringValue().as("createdDate"),
                        board.member.photoUrl.as("profilePhotoUrl"),
                        board.member.profileName.as("profileName"),
                        board.boardPhoto.photoUrl.as("contentPhotoUrl"),
                        board.content.as("content"),
                        board.like.as("likes"),
                        // 사용자가 해당 게시물을 좋아요 했으므로 항상 true
                        Expressions.asBoolean(true).as("isLiked")
                ))
                .from(like)
                .join(like.board, board)
                .join(board.member, member).fetchJoin()
                .join(board.boardPhoto, boardPhoto).fetchJoin()
                .where(like.member.id.eq(myId)
                        .and(like.isLiked.isTrue()))
                .orderBy(board.createdAt.desc())
                .fetch();
    }
}
