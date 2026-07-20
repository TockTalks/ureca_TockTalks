package com.tocktalks.domain.room.controller;

import com.tocktalks.domain.room.dto.CreateRoomRequest;
import com.tocktalks.domain.room.dto.JoinByInviteCodeRequest;
import com.tocktalks.domain.room.dto.RoomParticipantResponse;
import com.tocktalks.domain.room.dto.RoomRankingResponse;
import com.tocktalks.domain.room.dto.RoomResponse;
import com.tocktalks.domain.room.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public RoomResponse createRoom(Authentication authentication, @RequestBody @Valid CreateRoomRequest request) {
        return roomService.createRoom(memberId(authentication), request);
    }

    @GetMapping
    public List<RoomResponse> getPublicRooms() {
        return roomService.getPublicRooms();
    }

    @GetMapping("/mine")
    public List<RoomResponse> getMyRooms(Authentication authentication) {
        return roomService.getMyRooms(memberId(authentication));
    }

    @GetMapping("/default")
    public RoomResponse getDefaultRoom() {
        return roomService.getDefaultRoom();
    }

    @GetMapping("/{roomId}/ranking")
    public List<RoomRankingResponse> getRanking(@PathVariable Long roomId) {
        return roomService.getRanking(roomId);
    }

    @GetMapping("/{roomId}")
    public RoomResponse getRoom(Authentication authentication, @PathVariable Long roomId) {
        return roomService.getRoomDetail(roomId, memberIdOrNull(authentication));
    }

    @PostMapping("/{roomId}/join")
    public RoomParticipantResponse joinRoom(Authentication authentication, @PathVariable Long roomId) {
        return roomService.joinRoomById(roomId, memberId(authentication));
    }

    @PostMapping("/join")
    public RoomParticipantResponse joinByInviteCode(Authentication authentication,
                                                      @RequestBody @Valid JoinByInviteCodeRequest request) {
        return roomService.joinRoomByInviteCode(request.inviteCode(), memberId(authentication));
    }

    @DeleteMapping("/{roomId}/participants/me")
    public void leaveRoom(Authentication authentication, @PathVariable Long roomId) {
        roomService.leaveRoom(roomId, memberId(authentication));
    }

    private Long memberId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }

    private Long memberIdOrNull(Authentication authentication) {
        return authentication != null && authentication.getPrincipal() instanceof Long memberId ? memberId : null;
    }
}
