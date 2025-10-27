package com.mss.prm_project.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table(name = "reading_list")
public class ReadingList extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reading_id")
    int readingId;

    @Column(name = "name", columnDefinition = "nvarchar(255)")
    String name;

    @Column(name = "description", columnDefinition = "nvarchar(550)")
    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User ownerUser;

    @ManyToMany
    @JoinTable(
            name = "reading_list_paper",
            joinColumns = @JoinColumn(name = "reading_id"),
            inverseJoinColumns = @JoinColumn(name = "paper_id")
    )
    Set<Paper> papers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "reading_list_viewers", // Bảng nối mới
            joinColumns = @JoinColumn(name = "reading_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    Set<User> viewers = new HashSet<>();

}
