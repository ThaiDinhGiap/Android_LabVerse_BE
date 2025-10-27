package com.mss.prm_project.service.serviceimpl;

import com.mss.prm_project.dto.PaperDTO;
import com.mss.prm_project.entity.Paper;
import com.mss.prm_project.entity.ReadingList;
import com.mss.prm_project.entity.User;
import com.mss.prm_project.model.PaperResponse;
import com.mss.prm_project.model.ReadingListDetailResponse;
import com.mss.prm_project.model.ReadingListResponse;
import com.mss.prm_project.repository.PaperRepository;
import com.mss.prm_project.repository.ReadingListRepository;
import com.mss.prm_project.repository.UserRepository;
import com.mss.prm_project.service.ReadingListService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class ReadingListServiceImpl implements ReadingListService {

    private final ReadingListRepository readingListRepository;
    private final PaperRepository paperRepository;
    private final UserRepository userRepository;

    public ReadingListServiceImpl(ReadingListRepository readingListRepository, PaperRepository paperRepository, UserRepository userRepository) {
        this.readingListRepository = readingListRepository;
        this.paperRepository = paperRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ReadingList getListIfOwner(int listId, int userId) {
        return readingListRepository.findByReadingIdAndOwnerUserUserId(listId, userId)
                .orElseThrow(() -> new AccessDeniedException("User is not the owner or Reading List not found."));
    }

    @Transactional
    @Override
    public ReadingListResponse createList(int ownerId, String name, String description) {
        User owner = userRepository.findById((long) ownerId)
                .orElseThrow(() -> new RuntimeException("Owner user not found."));

        // Ánh xạ từ Request (name, description) sang Entity
        ReadingList newList = ReadingList.builder()
                .name(name)
                .description(description)
                .ownerUser(owner)
                .papers(new HashSet<>())
                .build();

        newList = readingListRepository.save(newList);

        ReadingListResponse response = new ReadingListResponse();
        response.setReadingId(newList.getReadingId());
        response.setName(newList.getName());
        response.setDescription(newList.getDescription());
        response.setOwnerUserId(newList.getOwnerUser().getUserId());
        // Sử dụng .size() để lấy số lượng từ Set
        response.setPaperCount(newList.getPapers() != null ? newList.getPapers().size() : 0);

        return response;
    }

    @Override
    public List<ReadingListResponse> getListsByOwner(int userId) {
        List<ReadingList> readingLists = readingListRepository.findByOwnerUserUserId(userId);
        return readingLists.stream()
                .map(readingList -> {
                    ReadingListResponse response = new ReadingListResponse();
                    response.setReadingId(readingList.getReadingId());
                    response.setName(readingList.getName());
                    response.setDescription(readingList.getDescription());
                    response.setOwnerUserId(readingList.getOwnerUser().getUserId());
                    response.setPaperCount(readingList.getPapers() != null ? readingList.getPapers().size() : 0);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ReadingListDetailResponse getListDetails(int listId) {
        ReadingList list = readingListRepository.findByIdWithPapers(listId)
                .orElseThrow(() -> new RuntimeException("Reading List not found."));

        // Ánh xạ trực tiếp từ Entity sang Detail DTO
        ReadingListDetailResponse response = new ReadingListDetailResponse();
        response.setReadingId(list.getReadingId());
        response.setName(list.getName());
        response.setDescription(list.getDescription());
        response.setOwnerUserId(list.getOwnerUser().getUserId());
        response.setOwnerUsername(list.getOwnerUser().getUsername());

        // Ánh xạ Papers
        List<PaperResponse> paperResponses = list.getPapers().stream()
                .map(lists ->{
                    PaperResponse paperDTO = new PaperResponse();
                    paperDTO.setPaperId(lists.getPaperId());
                    paperDTO.setTitle(lists.getTitle());
                    paperDTO.setAuthor(lists.getAuthor());
                    paperDTO.setJournal(lists.getJournal());
                    return paperDTO;
                })
                .collect(Collectors.toList());

        response.setPapers(paperResponses);
        response.setPaperCount(paperResponses.size());

        return response;
    }

    @Override
    @Transactional
    public ReadingListDetailResponse addPaperToList(int listId, int paperId, int userId) {
        ReadingList list = getListIfOwner(listId, userId);

        Paper paper = paperRepository.findById((long) paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found."));

        if (!list.getPapers().add(paper)) {
            throw new IllegalArgumentException("Paper is already in this reading list.");
        }

        list = readingListRepository.save(list);

        // Ánh xạ trực tiếp từ Entity đã cập nhật sang Detail DTO
        ReadingListDetailResponse response = new ReadingListDetailResponse();
        response.setReadingId(list.getReadingId());
        response.setName(list.getName());
        response.setDescription(list.getDescription());
        response.setOwnerUserId(list.getOwnerUser().getUserId());
        response.setOwnerUsername(list.getOwnerUser().getUsername());

        // Ánh xạ Papers
        List<PaperResponse> paperResponses = list.getPapers().stream()
                .map(lists ->{
                    PaperResponse paperDTO = new PaperResponse();
                    paperDTO.setPaperId(lists.getPaperId());
                    paperDTO.setTitle(lists.getTitle());
                    paperDTO.setAuthor(lists.getAuthor());
                    paperDTO.setJournal(lists.getJournal());
                    return paperDTO;
                })
                .collect(Collectors.toList());

        response.setPapers(paperResponses);
        response.setPaperCount(paperResponses.size());

        return response;
    }

    @Override
    @Transactional
    public ReadingListDetailResponse removePaperFromList(int listId, int paperId, int userId) {
        ReadingList list = getListIfOwner(listId, userId);

        Paper paperToRemove = paperRepository.findById((long) paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found."));

        if (!list.getPapers().remove(paperToRemove)) {
            throw new IllegalArgumentException("Paper not found in the specified reading list.");
        }

        list = readingListRepository.save(list);

        // Ánh xạ trực tiếp từ Entity đã cập nhật sang Detail DTO
        ReadingListDetailResponse response = new ReadingListDetailResponse();
        response.setReadingId(list.getReadingId());
        response.setName(list.getName());
        response.setDescription(list.getDescription());
        response.setOwnerUserId(list.getOwnerUser().getUserId());
        response.setOwnerUsername(list.getOwnerUser().getUsername());

        // Ánh xạ Papers
        List<PaperResponse> paperResponses = list.getPapers().stream()
                .map(lists ->{
                    PaperResponse paperDTO = new PaperResponse();
                    paperDTO.setPaperId(lists.getPaperId());
                    paperDTO.setTitle(lists.getTitle());
                    paperDTO.setAuthor(lists.getAuthor());
                    paperDTO.setJournal(lists.getJournal());
                    return paperDTO;
                })
                .collect(Collectors.toList());

        response.setPapers(paperResponses);
        response.setPaperCount(paperResponses.size());

        return response;
    }

    @Override
    @Transactional
    public void deleteList(int listId, int userId) {
            ReadingList list = getListIfOwner(listId, userId);
            readingListRepository.delete(list);
    }

    @Transactional
    @Override
    public void addViewerToList(int listId, String invitedEmail, int ownerId) {
        // 1. Kiểm tra Quyền: Chỉ chủ sở hữu mới được thêm người xem
        // Giữ nguyên logic kiểm tra quyền của Owner (ownerId)
        ReadingList list = getListIfOwner(listId, ownerId);

        // 2. TÌM KIẾM User được mời bằng EMAIL
        User invitedUser = userRepository.findByEmail(invitedEmail)
                .orElseThrow(() -> new RuntimeException("Invited user not found with email: " + invitedEmail));

        int invitedUserId = invitedUser.getUserId(); // Lấy ID của người được mời sau khi tìm thấy

        // 3. Kiểm tra User được mời có phải là chủ sở hữu không
        if (list.getOwnerUser().getUserId() == invitedUserId) {
            throw new IllegalArgumentException("User is the owner and cannot be added as a viewer.");
        }

        // 4. Kiểm tra Trùng lặp (Sử dụng Set viewers)
        if (list.getViewers().contains(invitedUser)) {
            throw new IllegalArgumentException("User is already a viewer of this reading list.");
        }

        // 5. Thêm User vào Set viewers
        list.getViewers().add(invitedUser);

        // 6. Lưu List
        readingListRepository.save(list);
    }

    @Transactional
    @Override
    public void removeViewerFromList(int listId, int viewerId, int ownerId) {
        // 1. Kiểm tra Quyền: Chỉ chủ sở hữu mới được xóa người xem
        ReadingList list = getListIfOwner(listId, ownerId);

        // 2. Tìm User được xóa (viewer)
        User viewerToRemove = userRepository.findById((long) viewerId)
                .orElseThrow(() -> new RuntimeException("Viewer user not found."));

        // 3. Kiểm tra xem người xóa có phải là chủ sở hữu không (Chủ sở hữu không thể tự xóa mình khỏi viewer, dù sao cũng là owner)
        if (list.getOwnerUser().getUserId() == viewerId) {
            throw new IllegalArgumentException("Cannot remove the owner from the viewer list.");
        }

        // 4. Xóa User khỏi Set viewers và kiểm tra thất bại
        if (!list.getViewers().remove(viewerToRemove)) {
            // Set.remove() trả về false nếu phần tử không có trong Set
            throw new IllegalArgumentException("User is not currently a viewer of this reading list.");
        }

        // 5. Lưu List (Hibernate sẽ cập nhật bảng nối)
        readingListRepository.save(list);
    }
}
