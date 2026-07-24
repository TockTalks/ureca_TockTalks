package com.tocktalks.domain.auth.dto;

/**
 * 회원탈퇴 요청 정보.
 * 로컬 계정은 본인 확인을 위해 현재 비밀번호를 사용하고, 소셜 계정은 값을 보내지 않는다.
 */
public record MemberWithdrawalRequest(
        String currentPassword
) {
}
