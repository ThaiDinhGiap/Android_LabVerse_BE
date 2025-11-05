package com.mss.prm_project.service.serviceimpl;

import com.mss.prm_project.dto.ReadingProgressDTO;
import com.mss.prm_project.entity.Collection;
import com.mss.prm_project.entity.Paper;
import com.mss.prm_project.entity.ReadingProgress;
import com.mss.prm_project.entity.User;
import com.mss.prm_project.mapper.ReadingProgressMapper;
import com.mss.prm_project.repository.CollectionRepository;
import com.mss.prm_project.repository.PaperRepository;
import com.mss.prm_project.repository.ReadingProgressRepository;
import com.mss.prm_project.repository.UserRepository;
import com.mss.prm_project.service.ReadingProgressService;
import com.mss.prm_project.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadingProgressServiceImpl implements ReadingProgressService {
    private final ReadingProgressRepository readingProgressRepository;
    private final UserRepository userRepository;
    private final PaperRepository paperRepository;
    private final CollectionRepository collectionRepository;
    @Override
    public ReadingProgressDTO createReadingProgressForUserAndPaper(int collectionId, int paperId, int lastReadPage, int totalPages) {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUserName().get()).get();
        Paper paper = paperRepository.findById((long)paperId).orElse(null);
        Collection collection = collectionRepository.findById((long)collectionId).orElse(null);
        if (Objects.nonNull(paper) && collectionId >0) {
            ReadingProgress readingProgress = readingProgressRepository.findByUserUserIdAndPaperPaperIdCollectionCollectionId(user.getUserId(), paperId, collectionId);
            if (readingProgress == null) {
                readingProgress = new ReadingProgress();
                readingProgress.setUser(user);
                readingProgress.setPaper(paper);
                readingProgress.setLatestPage(lastReadPage);
                readingProgress.setCollection(collection);
                readingProgress.setProgressStatus("To Read");
            }
            else {
                if(lastReadPage > readingProgress.getLatestPage()){
                    readingProgress.setLatestPage(lastReadPage);
                    BigDecimal completionPercent = BigDecimal.valueOf(((double) lastReadPage / totalPages) * 100);
                    readingProgress.setCompletionPercent(completionPercent);
                }
                String progressStatus = (lastReadPage == totalPages) ? "Completed" : "Reading";
                readingProgress.setProgressStatus(progressStatus);
            }
            ReadingProgress savedProgress = readingProgressRepository.save(readingProgress);
            return ReadingProgressMapper.INSTANCE.toDTO(savedProgress);
        }
        return null;
    }

    @Override
    public List<ReadingProgressDTO> getAllReadingProgressByCollection(int collectionId, int paperId) {
        if(paperId != 0){
            return readingProgressRepository.findByAndPaperPaperIdCollectionCollectionId(paperId, collectionId)
                    .stream()
                    .map(ReadingProgressMapper.INSTANCE::toDTO)
                    .collect(Collectors.toList());
        }
        List<ReadingProgress> readingProgressList = readingProgressRepository.getAllReadingProgressByCollectionCollectionId(collectionId);
        return readingProgressList.stream().map(ReadingProgressMapper.INSTANCE::toDTO).collect(Collectors.toList());
    }
}
