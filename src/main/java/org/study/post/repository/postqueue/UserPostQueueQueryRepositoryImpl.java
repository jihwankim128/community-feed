package org.study.post.repository.postqueue;

import static org.study.post.repository.entity.like.QLikeEntity.likeEntity;
import static org.study.post.repository.entity.post.QPostEntity.postEntity;
import static org.study.post.repository.entity.post.QUserPostQueueEntity.userPostQueueEntity;
import static org.study.user.repository.entity.QUserEntity.userEntity;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.study.post.repository.entity.post.QPostEntity;
import org.study.post.ui.dto.GetPostContentResponseDto;
import org.study.user.repository.entity.QUserEntity;

@Repository
@RequiredArgsConstructor
public class UserPostQueueQueryRepositoryImpl implements UserPostQueueQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<GetPostContentResponseDto> getContentResponse(Long userId, Long lastContentId) {
        return queryFactory.select(
            Projections.fields(
                GetPostContentResponseDto.class,
                postEntity.id.as("id"),
                postEntity.content.as("content"),
                userEntity.id.as("userId"),
                userEntity.name.as("userName"),
                userEntity.profileImage.as("profileImage"),
                postEntity.regDt.as("createdAt"),
                postEntity.updDt.as("updatedAt"),
                postEntity.commentCount.as("commentCount"),
                postEntity.likeCount.as("likeCount"),
                likeEntity.isNotNull().as("isLikedByMe")
            ))
            .from(userPostQueueEntity)
            .join(postEntity).on(userPostQueueEntity.postId.eq(postEntity.id))
            .join(userEntity).on(userPostQueueEntity.authorId.eq(userEntity.id))
            .leftJoin(likeEntity).on(hasLike(userId))
            .where(
                userPostQueueEntity.userId.eq(userId),
                hasLastData(lastContentId)
            )
            .orderBy(userPostQueueEntity.postId.desc())
            .limit(20)
            .fetch();
    }

    private BooleanExpression hasLike(Long userId) {
        if (userId == null) {
            return null;
        }

        return postEntity.id
            .eq(likeEntity.id.targetId)
            .and(likeEntity.id.targetType.eq("POST"))
            .and(likeEntity.id.userId.eq(userId));
    }

    private BooleanExpression hasLastData(Long lastId) {
        if (lastId == null) {
            return null;
        }

        return postEntity.id.lt(lastId);
    }
}
