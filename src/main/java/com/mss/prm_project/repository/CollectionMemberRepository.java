package com.mss.prm_project.repository;

import com.mss.prm_project.entity.CollectionMember;
import com.mss.prm_project.entity.CollectionMemberId;
import com.mss.prm_project.entity.CollectionMember.MemberRole;
import com.mss.prm_project.entity.CollectionMember.JoinStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionMemberRepository extends JpaRepository<CollectionMember, CollectionMemberId> {

    // Tim tất cả collection của 1 user
    List<CollectionMember> findByUserUserId(int userId);

    // find 1 member cụ thể (để kiểm tra quyền/trạng thái)
    Optional<CollectionMember> findByCollectionCollectionIdAndUserUserId(int collectionId, int userId);

    Optional<CollectionMember> findByCollectionCollectionIdAndUserUserIdAndRole(int collectionId, int userId, MemberRole role);

    Optional<CollectionMember> findByCollectionCollectionIdAndUserUserIdAndStatus(int collectionId, int userId, JoinStatus status);
}