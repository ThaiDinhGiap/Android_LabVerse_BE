package com.mss.prm_project.repository;

import com.mss.prm_project.entity.ReadingProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, Integer> {
    @Query("""
        SELECT rp
        FROM ReadingProgress rp
        WHERE rp.user.userId = :userId
        AND rp.paper.paperId = :paperId
        AND rp.collection.collectionId = :collectionId
    """)
    ReadingProgress findByUserUserIdAndPaperPaperIdCollectionCollectionId(
            @Param("userId") int userId,
            @Param("paperId") int paperId,
            @Param("collectionId") int collectionId
    );
    @Query("""
        SELECT rp
        FROM ReadingProgress rp WHERE
        rp.paper.paperId = :paperId
        AND rp.collection.collectionId = :collectionId
    """)
    List<ReadingProgress>  findByAndPaperPaperIdCollectionCollectionId(
            @Param("paperId") int paperId,
            @Param("collectionId") int collectionId
    );
    List<ReadingProgress> getAllReadingProgressByCollectionCollectionId(int collectionId);

    @Query("""
        SELECT rp
        FROM ReadingProgress rp
        WHERE rp.paper.paperId = :paperId
        AND rp.user.userId = :userId
    """)
    List<ReadingProgress>  findByUserUserIdAndPaperPaperId( @Param("userId") int userId,@Param("paperId") int paperId);

    List<ReadingProgress> getAllByUserUserId(int userId);

    List<ReadingProgress> getAllByUserUserIdOrderByUpdatedAt(int userId);

}
