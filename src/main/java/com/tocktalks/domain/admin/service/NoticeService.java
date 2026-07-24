package com.tocktalks.domain.admin.service;

import com.tocktalks.domain.admin.dto.request.NoticeCreateRequest;
import com.tocktalks.domain.admin.dto.response.NoticeResponse;
import com.tocktalks.domain.admin.entity.Notice;
import com.tocktalks.domain.admin.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public NoticeResponse createNotice(Long adminId, NoticeCreateRequest request){
        Notice notice = Notice.create(adminId, request.title(), request.content());
        return NoticeResponse.from(noticeRepository.save(notice));
    }

    public Page<NoticeResponse> getNotices(Pageable pageable){
        return noticeRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(NoticeResponse::from);
    }
}
