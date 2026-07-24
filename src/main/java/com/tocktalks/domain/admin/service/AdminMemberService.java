package com.tocktalks.domain.admin.service;

import com.tocktalks.domain.admin.dto.response.AdminMemberResponse;
import com.tocktalks.domain.auth.service.AuthService;
import com.tocktalks.domain.member.entity.Member;
import com.tocktalks.domain.member.repository.MemberRepository;
import com.tocktalks.domain.portfolio.repository.AssetHistoryRepository;
import com.tocktalks.domain.room.entity.Room;
import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.room.repository.RoomRepository;
import com.tocktalks.domain.trade.repository.HoldingRepository;
import com.tocktalks.domain.trade.repository.TransactionRepository;
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

    private final RoomRepository roomRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final HoldingRepository holdingRepository;
    private final TransactionRepository transactionRepository;
    private final AssetHistoryRepository assetHistoryRepository;
    private final AuthService authService;

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

    // 비밀번호 확인 없이 관리자가 직접 회원을 탈퇴 처리 (테스트/이상 계정 정리용)
    @Transactional
    public void withdrawMember(Long memberId) {
        authService.adminWithdraw(memberId);
    }

    public Page<AdminMemberResponse> getReportedMembers(Pageable pageable) {
        return memberRepository.findByReportedCountGreaterThanOrderByReportedCountDesc(0, pageable)
                .map(AdminMemberResponse::from);
    }

    //회원의 로비 시드머니 및 거래 내역 초기화
    @Transactional
    public void resetDefaultRoomAssets(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 회원탈퇴로 닫힌 계정의 자산 기록은 관리자 초기화 대상에서 제외한다.
        if (member.isWithdrawn()) {
            throw new IllegalArgumentException("탈퇴한 회원의 자산은 초기화할 수 없습니다.");
        }

        Room defaultRoom = roomRepository.findByIsDefaultTrue()
                .orElseThrow(() -> new IllegalStateException("기본 방을 찾을 수 없습니다."));

        RoomParticipant participant = roomParticipantRepository
                .findByRoomIdAndMemberIdAndStatus(defaultRoom.getId(), memberId, "ACTIVE")
                .orElseThrow(() -> new IllegalArgumentException("해당 회원의 기본 방 참가 정보를 찾을 수 없습니다."));

        holdingRepository.deleteAllByRoomParticipantId(participant.getId());
        transactionRepository.deleteAllByRoomParticipantId(participant.getId());
        assetHistoryRepository.deleteAllByRoomParticipantId(participant.getId());

        participant.resetBalance();
    }

}
