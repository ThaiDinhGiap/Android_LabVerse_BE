package com.mss.prm_project.service.serviceimpl;

import com.mss.prm_project.dto.FavoritePaperDTO;
import com.mss.prm_project.dto.FileDTO;
import com.mss.prm_project.dto.PaperDTO;
import com.mss.prm_project.entity.*;
import com.mss.prm_project.mapper.FavouriteMapper;
import com.mss.prm_project.entity.FavoritePaper;
import com.mss.prm_project.entity.File;
import com.mss.prm_project.entity.Paper;
import com.mss.prm_project.entity.User;
import com.mss.prm_project.mapper.PaperMapper;
import com.mss.prm_project.repository.*;
import com.mss.prm_project.repository.FavoritePaperRepository;
import com.mss.prm_project.repository.FileRepository;
import com.mss.prm_project.repository.PaperRepository;
import com.mss.prm_project.repository.UserRepository;
import com.mss.prm_project.service.PaperService;
import com.mss.prm_project.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
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
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PaperServiceImpl implements PaperService {
    private final PaperRepository paperRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final S3ServiceV2 s3ServiceV2;
    private final FavoritePaperRepository favoritePaperRepository;
    private final CollectionRepository collectionRepository;

    @Override
    public List<PaperDTO> getTop10NewestUnreadPapers(int userId) {
        if (!userRepository.existsById((long) userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return paperRepository.findUnreadByUser(userId).stream().map(PaperMapper.INSTANCE::toDTO).toList();
    }

//    @Override
//    public List<PaperDTO> getPaperByUserId(int userId) {
//        List<PaperDTO> resultList = new ArrayList<>();
//        if (userRepository.existsById((long) userId)) {
//            List <Paper> paperList = paperRepository.findByUserUserId(userId);
//            for (Paper paper : paperList) {
//                PaperDTO dto = PaperMapper.INSTANCE.toDTO(paper);
//                File file = fileRepository.findByPaperPaperId(paper.getPaperId());
//                if (file != null) {
//                    dto.setFileUrl(file.getFileUrl());
//                }
//                resultList.add(dto);
//            }
//        }
//        return resultList;
//    }

    @Override
    public PaperDTO insertPaper(PaperDTO dto, MultipartFile multipartFile) throws IOException {
        File file = new File();
        file.setFileUrl(s3ServiceV2.uploadFile(multipartFile));
//        file.setFileUrl("https://prm392-labverse.s3.ap-southeast-2.amazonaws.com/uploads/1760692462213_erd_prm.drawio.pdf");
        File savedfile = fileRepository.save(file);
        Paper paper = PaperMapper.INSTANCE.toEntity(dto);
        String username = SecurityUtils.getCurrentUserName().get();
        User user = userRepository.findByUsername(username).get();
        paper.setUser(user);
        Paper savedPaper = paperRepository.save(paper);
        savedfile.setPaper(savedPaper);
        fileRepository.save(savedfile);
        return PaperMapper.INSTANCE.toDTO(savedPaper);
    }

    @Override
    public List<PaperDTO> getPaperByUserId(
            String q,
            String author,
            String journal,
            Integer priority,
            String publisher,
            String dateStr,
            String fromDateStr,
            String toDateStr,
            Integer userId
    ) {
        List<Paper> papers = filterPapers(
                q, author, journal, priority, publisher,
                dateStr, fromDateStr, toDateStr,
                userId
        );

        List<PaperDTO> resultList = new ArrayList<>();

        for (Paper paper : papers) {
            PaperDTO dto = PaperMapper.INSTANCE.toDTO(paper);

            File file = fileRepository.findByPaperPaperId(paper.getPaperId());
            if (file != null) {
                dto.setFileUrl(file.getFileUrl());
            }

            resultList.add(dto);
        }

        return resultList;
    }

    private List<Paper> filterPapers(
            String q,
            String author,
            String journal,
            Integer priority,
            String publisher,
            String dateStr,
            String fromDateStr,
            String toDateStr,
            Integer userId
    ) {
        LocalDate dateFilter = (dateStr != null && !dateStr.isEmpty())
                ? LocalDate.parse(dateStr)
                : null;
        LocalDateTime fromDateTime = (fromDateStr != null && !fromDateStr.isEmpty())
                ? LocalDate.parse(fromDateStr).atStartOfDay()
                : null;
        LocalDateTime toDateTime = (toDateStr != null && !toDateStr.isEmpty())
                ? LocalDate.parse(toDateStr).atTime(LocalTime.MAX)
                : null;

        if (fromDateTime != null && toDateTime != null && fromDateTime.isAfter(toDateTime)) {
            throw new IllegalArgumentException("fromDate must not be after toDate");
        }

        return paperRepository.searchPapers(
                q,
                author,
                journal,
                priority,
                publisher,
                dateFilter,
                fromDateTime,
                toDateTime,
                userId
        );
    }



    @Override
    public List<PaperDTO> getPaperByPriority(long collectionid, int priority) {
        Collection collection = collectionRepository.findById(collectionid).get();
        List<Paper> paperList = collection.getPapers();
        if(Objects.isNull(priority)){
            return paperList.stream().map(PaperMapper.INSTANCE::toDTO).toList();
        }else{
            return null;
        }
    }


    @Override
    public List<PaperDTO> getUpdatedPapers(LocalDateTime lastSync) {
        return paperRepository.findAllUpdatedAfter(lastSync).stream()
                .map(PaperMapper.INSTANCE::toDTO)
                .toList();
    }

    @Override
    public FavoritePaperDTO addtoFavoritePapers(long userId, long paperId) {
        User user = userRepository.findById(userId).orElseThrow(null);
        Paper paper = paperRepository.findById(paperId).orElseThrow(null);
        if (user == null || paper == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or Paper not found");
        }
        FavoritePaper favoritePaper = new FavoritePaper();
        favoritePaper.setUser(user);
        favoritePaper.setPaper(paper);
        FavoritePaper savedfavoritePaper = favoritePaperRepository.save(favoritePaper);
        return FavouriteMapper.INSTANCE.toDTO(savedfavoritePaper);
    }

    @Override
    public boolean deletePaper(long paperId) {
        Paper paper = paperRepository.findById(paperId).orElseThrow(null);
        if (paper == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper not found");
        }
        paperRepository.delete(paper);
        return true;
    }

    @Override
    public boolean deleteFavoritePaper(long paperId, long userId) {
        Paper paper = favoritePaperRepository.findByUserUserIdAndPaperPaperId(userId, paperId).getPaper();
        if (paper == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper not found");
        }
        paperRepository.delete(paper);
        return true;
    }

    @Override
    public PaperDTO findByPaperId(long paperId) {
        Paper paper = paperRepository.findById(paperId).orElseThrow(null);
        PaperDTO dto =  PaperMapper.INSTANCE.toDTO(paper);
        File file = fileRepository.findByPaperPaperId(paper.getPaperId());
        if (file != null) {
            dto.setFileUrl(file.getFileUrl());
        }
        return dto;
    }

    // Build link DOI (không lưu DB)
    public String doiLink(String doi) {
        if (doi == null) return null;
        String d = doi.trim();
        return d.isEmpty() ? null : "https://doi.org/" + d;
    }

    public String toAPA(Paper p) {
        String authors = apaAuthors(p.getAuthor());
        String year = (p.getPublishDate() != null) ? String.valueOf(p.getPublishDate().getYear()) : "n.d.";
        String title = nz(p.getTitle());
        String journal = nz(p.getJournal());
        String link = (p.getDoi() != null && !p.getDoi().isBlank()) ? doiLink(p.getDoi()) : null;

        return String.format("%s (%s). %s. %s.%s",
                authors, year, title, journal,
                link != null ? " " + link : "");
    }

    public String toMLA(Paper p) {
        String authors = mlaAuthors(p.getAuthor());
        String year = (p.getPublishDate() != null) ? String.valueOf(p.getPublishDate().getYear()) : "";
        String title = nz(p.getTitle());
        String journal = nz(p.getJournal());
        String link = (p.getDoi() != null && !p.getDoi().isBlank()) ? doiLink(p.getDoi()) : null;

        // MLA bản demo: thêm link DOI nếu có
        return String.format("%s \"%s.\" %s, %s.%s",
                authors, title, journal, year,
                link != null ? " " + link : "");
    }

    public String toBibTeX(Paper p) {
        String key = buildBibKey(p);
        String year = (p.getPublishDate() != null) ? String.valueOf(p.getPublishDate().getYear()) : "";
        StringBuilder sb = new StringBuilder();
        sb.append("@article{").append(key).append(",\n")
                .append("  title = {").append(escape(nz(p.getTitle()))).append("},\n")
                .append("  author = {").append(escape(nz(p.getAuthor()))).append("},\n");
        if (p.getJournal() != null && !p.getJournal().isBlank())
            sb.append("  journal = {").append(escape(p.getJournal())).append("},\n");
        if (!year.isBlank())
            sb.append("  year = {").append(year).append("},\n");
        if (p.getDoi() != null && !p.getDoi().isBlank())
            sb.append("  doi = {").append(escape(p.getDoi())).append("},\n");
        sb.append("}");
        return sb.toString();
    }

    // ===== helpers =====
    private String nz(String s){ return s == null ? "" : s; }
    private String escape(String s){ return s.replace("{", "\\{").replace("}", "\\}"); }
    private String buildBibKey(Paper p){
        String last = safeLastName(p.getAuthor());
        String year = (p.getPublishDate() != null) ? String.valueOf(p.getPublishDate().getYear()) : "";
        if (last.isBlank()) last = "Paper";
        if (year.isBlank()) year = String.valueOf(System.currentTimeMillis());
        return (last + year).replaceAll("\\s+", "");
    }
    private String safeLastName(String authorField){
        if (authorField == null || authorField.isBlank()) return "";
        String firstAuthor = authorField.split(";")[0].trim();
        String[] parts = firstAuthor.split("\\s+");
        return parts.length == 0 ? "" : parts[parts.length - 1];
        // Bạn có thể cải tiến tách họ tên chuẩn hơn sau
    }
    private String apaAuthors(String s){ return nz(s); }
    private String mlaAuthors(String s){ return nz(s); }
}
