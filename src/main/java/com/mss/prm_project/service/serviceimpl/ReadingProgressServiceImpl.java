package com.mss.prm_project.service.serviceimpl;

import com.mss.prm_project.dto.ReadingProgressDTO;
import com.mss.prm_project.entity.*;
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
import java.util.ArrayList;
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
        Paper paper = paperRepository.findById((long)paperId).orElseThrow(()-> new IllegalArgumentException("Paper not found"));
        Collection collection = collectionRepository.findById((long)collectionId).orElse(null);
        ReadingProgress readingProgress = null;
        if (collectionId >0) {
            readingProgress = readingProgressRepository.findByUserUserIdAndPaperPaperIdCollectionCollectionId(user.getUserId(), paperId, collectionId);
        }else {
            readingProgress = readingProgressRepository.findByUserUserIdAndPaperPaperId(user.getUserId(), paperId);
        }
        if (readingProgress == null) {
            readingProgress = new ReadingProgress();
            readingProgress.setUser(user);
            readingProgress.setPaper(paper);
            readingProgress.setLatestPage(0);
            if(Objects.nonNull(collection)) {
                readingProgress.setCollection(collection);
            }
            readingProgress.setProgressStatus("To Read");
            readingProgress.setCompletionPercent(BigDecimal.valueOf(0));
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

    @Override
    public List<ReadingProgressDTO> getAllReadingProgressByCollection(int collectionId) {
        Collection collection = collectionRepository.findById((long)collectionId)
                                .orElseThrow(()-> new IllegalArgumentException("Collection not found"));
        List<CollectionMember> members = collection.getMembers();
        List<User> users = members.stream()
                                .map(CollectionMember::getUser)
                                .toList();
        List<Paper> papers = collection.getPapers();
        List<ReadingProgress> results = new ArrayList<>();
        for(User user : users){
            for(Paper paper : papers) {
                ReadingProgress readingProgress = readingProgressRepository.findByUserUserIdAndPaperPaperIdCollectionCollectionId(
                        user.getUserId(),
                        paper.getPaperId(),
                        collectionId
                );
                if (readingProgress == null) {
                    readingProgress = new ReadingProgress();
                    readingProgress.setUser(user);
                    readingProgress.setPaper(paper);
                    readingProgress.setCollection(collection);
                    readingProgress.setLatestPage(0);
                    readingProgress.setProgressStatus("To Read");
                    readingProgress.setCompletionPercent(BigDecimal.valueOf(0));
                    ReadingProgress item = readingProgressRepository.save(readingProgress);
                    results.add(item);
                }else{
                    results.add(readingProgress);
                }
            }
        }
        return results.stream().map(ReadingProgressMapper.INSTANCE::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ReadingProgressDTO> getPersonalReadingProgress() {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUserName().get()).
                                    orElseThrow(()-> new IllegalArgumentException("User not found"));
        List<Paper> papers = user.getPaper();
        List<ReadingProgress> results = new ArrayList<>();
        if(papers != null) {
            for(Paper paper : papers){
                ReadingProgress readingProgress = readingProgressRepository.findByUserUserIdAndPaperPaperId(
                        user.getUserId(),
                        paper.getPaperId()
                );
                if (readingProgress == null) {
                    readingProgress = new ReadingProgress();
                    readingProgress.setUser(user);
                    readingProgress.setPaper(paper);
                    readingProgress.setLatestPage(0);
                    readingProgress.setProgressStatus("To Read");
                    readingProgress.setCompletionPercent(BigDecimal.valueOf(0));
                    ReadingProgress saveReadingProgress = readingProgressRepository.save(readingProgress);
                    results.add(saveReadingProgress);
                }
                else{
                    results.add(readingProgress);
                }
                return results.stream().map(ReadingProgressMapper.INSTANCE::toDTO).collect(Collectors.toList());
            }
        }
        return null;
    }
}
