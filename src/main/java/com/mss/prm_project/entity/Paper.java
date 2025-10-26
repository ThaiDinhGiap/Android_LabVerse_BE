package com.mss.prm_project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table(name = "papers")
public class Paper extends  BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paper_id")
    int paperId;

    @Column(name = "title", columnDefinition = "nvarchar(255)")
    String title;

    @Column(name = "author", columnDefinition = "nvarchar(255)")
    String author;

    @Column(name = "journal", columnDefinition = "nvarchar(255)")
    String journal;

    @Column(name = "publisher", columnDefinition = "nvarchar(255)")
    String publisher;

    @Column(name = "publish_date")
    LocalDateTime publishDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "is_Offline")
    boolean isOffline;

    @Column(name = "priority")
    int priority;

    @ManyToMany(mappedBy = "papers")
    Set<Collection> collections = new HashSet<>();
}
