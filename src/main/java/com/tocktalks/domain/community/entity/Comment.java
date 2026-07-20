package com.tocktalks.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private Comment(Long postId, Long memberId, String content){
        this.postId = postId;
        this.memberId = memberId;
        this.content = content;
        this.likeCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Comment create(Long postId, Long memberId, String content){
        return new Comment(postId, memberId, content);
    }

    public void updateContent(String content){
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isOwnedBy(Long memberId){
        return this.memberId.equals(memberId);
    }

    public void increaseLikeCount(){
        this.likeCount++;
    }

    public void decreaseLikeCount(){
        if(this.likeCount > 0) this.likeCount--;
    }
}
