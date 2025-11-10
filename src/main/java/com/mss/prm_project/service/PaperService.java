package com.mss.prm_project.service;

import com.mss.prm_project.dto.FavoritePaperDTO;
import com.mss.prm_project.dto.PaperDTO;
import com.mss.prm_project.entity.Paper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface PaperService {
    List<PaperDTO> getTop10NewestUnreadPapers(int userId);
    Paper getPaperById(int paperId);
//     List<PaperDTO> getPaperByUserId(int userId);

    PaperDTO insertPaper(PaperDTO dto, MultipartFile file) throws IOException;
    List<PaperDTO> getPaperByUserId(String q,
                                    String author,
                                    String journal,
                                    Integer priority,
                                    String publisher,
                                    String date,
                                    String fromDate,
                                    String toDate,
                                    Integer userId);

    List<PaperDTO> getUpdatedPapers(LocalDateTime lastSync);

    List<PaperDTO> getPaperByPriority(long collectionid ,int priority);

    FavoritePaperDTO addToFavoritePapers(long paperId);

    boolean deletePaper(long paperId);

    boolean deleteFavoritePaper(long favoritePaperId);

    List<FavoritePaperDTO> getFavoriteByUser();

    List<PaperDTO> getNewestAdded();

    PaperDTO findByPaperId(long paperId);

    PaperDTO changePaperPriority(long paper, int priority);
}
