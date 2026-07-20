package com.tocktalks.domain.admin.service;

import com.tocktalks.domain.admin.repository.ReportRepository;
import com.tocktalks.domain.community.repository.CommentRepository;
import com.tocktalks.domain.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private static final String STATUS_PENDING = "pending";

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

}
