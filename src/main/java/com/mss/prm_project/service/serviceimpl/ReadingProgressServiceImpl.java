package com.mss.prm_project.service.serviceimpl;

import com.mss.prm_project.dto.ReadingProgressDTO;
import com.mss.prm_project.entity.Paper;
import com.mss.prm_project.entity.ReadingProgress;
import com.mss.prm_project.entity.User;
import com.mss.prm_project.mapper.ReadingProgressMapper;
import com.mss.prm_project.repository.PaperRepository;
import com.mss.prm_project.repository.ReadingProgressRepository;
import com.mss.prm_project.repository.UserRepository;
import com.mss.prm_project.service.ReadingProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ReadingProgressServiceImpl implements ReadingProgressService {
    private final ReadingProgressRepository readingProgressRepository;
    private final UserRepository userRepository;
    private final PaperRepository paperRepository;
    @Override
    public ReadingProgressDTO createReadingProgressForUserAndPaper(Long userId, Long paperId, int lastReadPage, int totalPages) {
        User user = userRepository.findById((long)userId).orElse(null);
        Paper paper = paperRepository.findById(paperId).orElse(null);
        if (user != null && paper != null) {
            ReadingProgress readingProgress = readingProgressRepository.findByUserUserIdAndPaperPaperId(userId, paperId);
            if (readingProgress == null) {
                readingProgress = new ReadingProgress();
                readingProgress.setUser(user);
                readingProgress.setPaper(paper);
                readingProgress.setLatestPage(lastReadPage);
                if(lastReadPage == 0){
                    readingProgress.setProgressStatus("To Read");
                } else {
                    String progressStatus = (lastReadPage == totalPages) ? "Completed" : "Reading";
                    readingProgress.setProgressStatus(progressStatus);
                }
            }
            else {
                if(lastReadPage > readingProgress.getLatestPage()){
                    readingProgress.setLatestPage(lastReadPage);
                }
                String progressStatus = (lastReadPage == totalPages) ? "Completed" : "Reading";
                readingProgress.setProgressStatus(progressStatus);
            }
            BigDecimal completionPercent = BigDecimal.valueOf(((double) lastReadPage / totalPages) * 100);
            readingProgress.setCompletionPercent(completionPercent);
            ReadingProgress savedProgress = readingProgressRepository.save(readingProgress);
            return ReadingProgressMapper.INSTANCE.toDTO(savedProgress);
        }
        return null;
    }
}
