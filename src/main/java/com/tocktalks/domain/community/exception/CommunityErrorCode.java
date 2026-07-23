package com.tocktalks.domain.community.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommunityErrorCode {

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    POST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "본인 게시글만 수정/삭제할 수 있습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "본인 댓글만 수정/삭제할 수 있습니다."),
    MEMBER_BLOCKED(HttpStatus.FORBIDDEN, "차단된 회원은 게시글/댓글을 작성할 수 없습니다.");

    private final HttpStatus status;
    private final String message;

}
