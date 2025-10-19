package com.mss.prm_project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "collection_members")
public class CollectionMember extends BaseEntity {

    @EmbeddedId
    CollectionMemberId id;

    // có thể phát  triển thêm trao quyền admin PI cho người khác hoặc nhiều Pi nhiều role (Viewer, Editor)
    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false)
    MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "join_status", nullable = false)
    JoinStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("collectionId")
    @JoinColumn(name = "collection_id", nullable = false)
    Collection collection;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    public enum MemberRole { PI, MEMBER }

    // Enum cho Trạng thái
    public enum JoinStatus { PENDING, JOINED, REJECTED }
}