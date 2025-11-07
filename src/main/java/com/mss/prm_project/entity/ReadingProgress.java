package com.mss.prm_project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "reading_progress",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "paper_id"})
        }
)
public class ReadingProgress extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reading_progress_id")
    int readingProgressId;

    @Column(name = "progress_status", columnDefinition = "nvarchar(255)")
    String progressStatus;

    @Column(name = "completion_percent", precision = 5, scale = 2)
    BigDecimal completionPercent;

    @Column(name = "latest_page")
    int latestPage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id", nullable = false)
    Paper paper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    Collection collection;
}
