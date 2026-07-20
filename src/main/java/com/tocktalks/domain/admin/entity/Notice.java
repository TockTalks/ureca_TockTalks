package com.tocktalks.domain.admin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "notice")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;
    
    public static Notice create(Long adminId, @NotBlank @Size(max = 200) String title, @NotBlank String content) {
        return new Notice(null, adminId, title, content, LocalDate.now());
    }
    
    private Notice(Long id, Long adminId, String title, String content, LocalDate createdAt) {
        this.id = id;
        this.adminId = adminId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }
}
