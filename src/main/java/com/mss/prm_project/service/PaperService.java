package com.mss.prm_project.service;

import com.mss.prm_project.dto.FavoritePaperDTO;
import com.mss.prm_project.dto.PaperDTO;
import com.mss.prm_project.entity.FavoritePaper;
import com.mss.prm_project.entity.Paper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PaperService {
     List<PaperDTO> getTop10NewestUnreadPapers(int userId);

     List<PaperDTO> getPaperByUserId(int userId);

     PaperDTO insertPaper(PaperDTO dto, MultipartFile file) throws IOException;

     List<PaperDTO> getPaperByPriority(long collectionid ,int priority);

    FavoritePaperDTO addtoFavoritePapers(long userId, long paperId);

    boolean deletePaper(long paperId);

    boolean deleteFavoritePaper(long paperId, long userId);

    PaperDTO findByPaperId(long paperId);

}
