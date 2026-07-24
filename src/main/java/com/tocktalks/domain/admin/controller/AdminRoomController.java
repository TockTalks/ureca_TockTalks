package com.tocktalks.domain.admin.controller;

import com.tocktalks.domain.room.service.RoomService;
import com.tocktalks.global.security.LoginMemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin/rooms")
@RequiredArgsConstructor
public class AdminRoomController {

    private final RoomService roomService;

    //방 강제 종료
    @PostMapping("/{roomId}/terminate")
    public ResponseEntity<Void> terminateRoom(
            @PathVariable Long roomId,
            @LoginMemberId Long adminId
    ) {
        roomService.terminateRoomByAdmin(roomId, adminId);
        return ResponseEntity.noContent().build();
    }

    // 방 삭제
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Long roomId,
            @LoginMemberId Long adminId
    ) {
        roomService.deleteRoomByAdmin(roomId, adminId);
        return ResponseEntity.noContent().build();
    }
}
