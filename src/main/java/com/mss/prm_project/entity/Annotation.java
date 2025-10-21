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
@Table(name = "annotations")
public class Annotation extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "annotation_id")
    long annotationId;

    @Column(name = "annotation_name", length = 500)
    String annotationName;

    @Column(name = "annotation_url", length = 500)
    String annotationUrl;

    @ManyToMany
    @JoinTable(
            name = "reader_annotations",
            joinColumns = @JoinColumn(name = "annotation_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    Set<User> readers = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id", nullable = false)
    Paper paper;

}
