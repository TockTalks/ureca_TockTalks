package com.tocktalks.domain.admin.service;

import com.tocktalks.domain.admin.dto.response.AdminMemberResponse;
import com.tocktalks.domain.member.entity.Member;
import com.tocktalks.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMemberService {

    private final MemberRepository memberRepository;

    // 전체 회원 검색 (keyword 없으면 전체 조회)
    public Page<AdminMemberResponse> getMembers(String keyword, Pageable pageable) {
        Page<Member> members = StringUtils.hasText(keyword)
                ? memberRepository.findByNicknameContainingOrEmailContaining(keyword, keyword, pageable)
                : memberRepository.findAll(pageable);

        return members.map(AdminMemberResponse::from);
    }

    // 회원 상세 상태 조회
    public AdminMemberResponse getMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return AdminMemberResponse.from(member);
    }

    // 회원 차단 처리 (거래/게시 금지)
    @Transactional
    public void blockMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        member.block();
    }
}