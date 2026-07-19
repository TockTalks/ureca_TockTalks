package com.tocktalks.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_like",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_post_like_post_member",
                columnNames = {"post_id", "member_id"}
        ))

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private PostLike(Long postId, Long memberId){
        this.postId = postId;
        this.memberId = memberId;
        this.createdAt = LocalDateTime.now();
    }

    public static PostLike create(Long postId, Long memberId){
        return new PostLike(postId, memberId);
    }

}
