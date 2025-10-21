package com.mss.prm_project.repository;

import com.mss.prm_project.entity.Collection;
import com.mss.prm_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

    // lấy tất cả collection mà 1 user là thành viên
//    @Query("SELECT c from Collection c JOIN c.users u where u = :user")
//    List<Collection> findAllCollectionByMember(@Param("user") User user);

}
