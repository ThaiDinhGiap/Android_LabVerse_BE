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
@Table(name = "collections")
public class Collection extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collection_id")
    int collectionId;

    @Column(name = "name", columnDefinition = "nvarchar(255)")
    String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User ownerUser;

    @ManyToMany
    @JoinTable(
            name = "collection_papers",
            joinColumns = @JoinColumn(name = "collection_id"),
            inverseJoinColumns = @JoinColumn(name = "paper_id")
    )
    Set<Paper> papers = new HashSet<>();

//    @ManyToMany
//    @JoinTable(
//            name = "collection_members",
//            joinColumns = @JoinColumn(name = "collection_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id")
//    )
//    Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<CollectionMember> members = new HashSet<>();
}
