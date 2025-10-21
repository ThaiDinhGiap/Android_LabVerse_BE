package com.mss.prm_project.service.serviceimpl;

import com.mss.prm_project.dto.FileDTO;
import com.mss.prm_project.dto.PaperDTO;
import com.mss.prm_project.entity.File;
import com.mss.prm_project.entity.Paper;
import com.mss.prm_project.mapper.FileMapper;
import com.mss.prm_project.mapper.PaperMapper;
import com.mss.prm_project.mapper.UserMapper;
import com.mss.prm_project.repository.FileRepository;
import com.mss.prm_project.repository.PaperRepository;
import com.mss.prm_project.repository.UserRepository;
import com.mss.prm_project.service.PaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaperServiceImpl implements PaperService {
    private final PaperRepository paperRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final S3ServiceV2 s3ServiceV2;

    @Override
    public List<Paper> getTop10NewestUnreadPapers(int userId) {
        if (!userRepository.existsById((long) userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        var page = paperRepository.findUnreadByUser(
                userId,
                PageRequest.of(0, 10, Sort.by("publishDate").descending())
        );
        return page.getContent();
    }

    @Override
    public List<PaperDTO> getPaperByUserId(int userId) {
        List<PaperDTO> resultList = new ArrayList<>();
        if (userRepository.existsById((long) userId)) {
            List <Paper> paperList = paperRepository.findByUserUserId(userId);
            for (Paper paper : paperList) {
                PaperDTO dto = PaperMapper.INSTANCE.toDTO(paper);
                File file = fileRepository.findByPaperPaperId(paper.getPaperId());
                if (file != null) {
                    dto.setFileUrl(file.getFileUrl());
                }
                resultList.add(dto);
            }
        }
        return resultList;
    }

    @Override
    public PaperDTO insertPaper(PaperDTO dto, MultipartFile multipartFile) throws IOException {
        File file = new File();
//        file.setFileUrl(s3ServiceV2.uploadFile(multipartFile));
        file.setFileUrl("https://prm392-labverse.s3.ap-southeast-2.amazonaws.com/uploads/1760692462213_erd_prm.drawio.pdf");
        File savedfile = fileRepository.save(file);
        Paper paper = PaperMapper.INSTANCE.toEntity(dto);
        Paper savedPaper = paperRepository.save(paper);
        savedfile.setPaper(savedPaper);
        fileRepository.save(savedfile);
        return PaperMapper.INSTANCE.toDTO(savedPaper);
    }

    @Override
    public List<PaperDTO> getPapers(String q, String author, String journal, Integer priority, String dateStr, String fromDateStr, String toDateStr) {
        LocalDate dateFilter = (dateStr != null && !dateStr.isEmpty())
                ? LocalDate.parse(dateStr) : null;

        LocalDateTime fromDateTime = (fromDateStr != null && !fromDateStr.isEmpty())
                ? LocalDate.parse(fromDateStr).atStartOfDay()
                : null;
        LocalDateTime toDateTime = (toDateStr != null && !toDateStr.isEmpty())
                ? LocalDate.parse(toDateStr).atTime(LocalTime.MAX)
                : null;

        if (fromDateTime != null && toDateTime != null && fromDateTime.isAfter(toDateTime)) {
            throw new IllegalArgumentException("fromDate must not be after toDate");
        }

        List<Paper> papers = paperRepository.searchPapers(q, author, journal, priority, dateFilter, fromDateTime, toDateTime);
        return papers.stream()
                .map(PaperMapper.INSTANCE::toDTO)
                .toList();
    }


}
