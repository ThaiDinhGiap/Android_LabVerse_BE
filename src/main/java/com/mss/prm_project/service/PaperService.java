package com.mss.prm_project.service;

import com.mss.prm_project.dto.PaperDTO;
import com.mss.prm_project.entity.Paper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

 public interface PaperService {
     List<Paper> getTop10NewestUnreadPapers(int userId);
     List<PaperDTO> getPaperByUserId(int userId);
     PaperDTO insertPaper(PaperDTO dto, MultipartFile file) throws IOException;

}
