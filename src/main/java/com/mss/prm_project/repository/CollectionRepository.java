package com.mss.prm_project.repository;

import com.mss.prm_project.entity.Collection;
import com.mss.prm_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

    // lấy tất cả collection mà 1 user là thành viên
//    @Query("SELECT c from Collection c JOIN c.users u where u = :user")
//    List<Collection> findAllCollectionByMember(@Param("user") User user);

    @Query("""
        SELECT c FROM Collection c 
        LEFT JOIN FETCH c.ownerUser o
        LEFT JOIN FETCH c.members cm 
        LEFT JOIN FETCH cm.user u 
        WHERE c.collectionId = :collectionId
    """)
    Optional<Collection> findCollectionDetailsById(@Param("collectionId") int collectionId);

    Collection findCollectionByCollectionId(int collectionId);

}
