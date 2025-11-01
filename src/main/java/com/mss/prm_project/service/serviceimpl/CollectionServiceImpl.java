package com.mss.prm_project.service.serviceimpl;

import com.mss.prm_project.entity.*;
import com.mss.prm_project.model.*;
import com.mss.prm_project.repository.CollectionMemberRepository;
import com.mss.prm_project.repository.CollectionRepository;
import com.mss.prm_project.repository.PaperRepository;
import com.mss.prm_project.repository.UserRepository;
import com.mss.prm_project.service.CollectionService;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Service
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final PaperRepository paperRepository;
    private final CollectionMemberRepository  collectionMemberRepository;

    public CollectionServiceImpl(CollectionRepository collectionRepository, UserRepository userRepository, PaperRepository paperRepository, CollectionMemberRepository collectionMemberRepository) {
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
        this.paperRepository = paperRepository;
        this.collectionMemberRepository = collectionMemberRepository;
    }

    @Transactional
    @Override
    public CollectionResponse createCollection(CollectionCreateRequest collectionCreateRequest, User user) {

        Collection collection = new Collection();
        collection.setName(collectionCreateRequest.getName());
        collection.setOwnerUser(user);
        Collection savedCollection = collectionRepository.save(collection);

        CollectionMember  collectionMember = new CollectionMember();

        CollectionMemberId  collectionMemberId = new CollectionMemberId(savedCollection.getCollectionId(), user.getUserId());
        collectionMember.setId(collectionMemberId);

        collectionMember.setCollection(savedCollection);
        collectionMember.setUser(user);

        collectionMember.setRole(CollectionMember.MemberRole.PI);
        collectionMember.setStatus(CollectionMember.JoinStatus.JOINED);

        collectionMemberRepository.save(collectionMember);

        CollectionResponse collectionResponse = new CollectionResponse();
        collectionResponse.setCollectionId(savedCollection.getCollectionId());
        collectionResponse.setName(savedCollection.getName());
        collectionResponse.setOwner(savedCollection.getOwnerUser().getUsername());
        collectionResponse.setMemberCount(savedCollection.getMembers().size());
        collectionResponse.setPaperCount(savedCollection.getPapers().size());

        return collectionResponse;
    }

    @Override
    public List<CollectionResponse> getMyCollections(User user) {

        List<CollectionMember> members = collectionMemberRepository.findByUserUserId(user.getUserId());

        return members.stream()
                .map(CollectionMember::getCollection)
                .map(collection ->{
                    CollectionResponse response = new CollectionResponse();
                    response.setCollectionId(collection.getCollectionId());
                    response.setName(collection.getName());
                    response.setOwner(collection.getOwnerUser().getUsername());
                    response.setMemberCount(collection.getMembers().size());
                    response.setPaperCount(collection.getPapers().size());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CollectionResponse addPaperCollection(int collectionID, int paperId, User user) {

        boolean isJoined = collectionMemberRepository
                .findByCollectionCollectionIdAndUserUserIdAndStatus(collectionID, user.getUserId(), CollectionMember.JoinStatus.JOINED)
                .isPresent();
        if (!isJoined) {
            throw new RuntimeException("Permission denied. User is not an active member of this collection.");
        }
        Collection collection = collectionRepository.findCollectionByCollectionId(collectionID);
        Paper paper = paperRepository.findByPaperId(paperId);

        collection.getPapers().add(paper);

        collectionRepository.save(collection);

        CollectionResponse collectionResponse = new CollectionResponse();
        collectionResponse.setCollectionId(collection.getCollectionId());
        collectionResponse.setName(collection.getName());
        collectionResponse.setOwner(collection.getOwnerUser().getUsername());
        collectionResponse.setMemberCount(collection.getMembers().size());
        collectionResponse.setPaperCount(collection.getPapers().size());

        return collectionResponse;
    }
    @Transactional
    @Override
    public void inviteMember(int collectionID, String invitedEmail, User user) {

        // 1. Kiểm tra Quyền (Trả về 403 Forbidden)
        collectionMemberRepository
                .findByCollectionCollectionIdAndUserUserIdAndRole(collectionID, user.getUserId(), CollectionMember.MemberRole.PI)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Permission denied. Only the PI can invite member of this collection."
                ));

        // 2. Kiểm tra Collection (Trả về 404 Not Found)
        Collection collection = collectionRepository.findById((long) collectionID)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Collection not found"
                ));

        // 3. Kiểm tra User (Trả về 404 Not Found)
        User invitedUser = userRepository.findByEmail(invitedEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        // Tìm bản ghi CollectionMember nếu nó đang ở trạng thái KHÔNG PHẢI REJECTED.
        boolean isAlreadyActiveOrPending = collectionMemberRepository
                .existsByCollectionCollectionIdAndUserUserIdAndStatusIsNot(
                        collectionID,
                        (long) invitedUser.getUserId(),
                        CollectionMember.JoinStatus.REJECTED
                );

        // Tạo ID kết hợp (Giữ lại nếu cần cho bước lưu)
        CollectionMemberId checkId = new CollectionMemberId(collectionID, invitedUser.getUserId());

        // 4. Kiểm tra Trùng lặp (Trả về 409 Conflict)
        if (isAlreadyActiveOrPending) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User is already part of the collection or has a pending invitation."
            );
        }

        // 5. Tạo và Lưu đối tượng CollectionMember
        CollectionMember collectionMember = new CollectionMember();
        collectionMember.setId(checkId);
        collectionMember.setCollection(collection);
        collectionMember.setUser(invitedUser);

        collectionMember.setRole(CollectionMember.MemberRole.MEMBER);
        collectionMember.setStatus(CollectionMember.JoinStatus.PENDING);

        collectionMemberRepository.save(collectionMember);
    }

    @Transactional
    @Override
    public CollectionDetailResponse getCollectionDetails(int collectionId, User user) {

        collectionMemberRepository
                .findByCollectionCollectionIdAndUserUserId(collectionId, user.getUserId())
                .orElseThrow(() -> new RuntimeException("Access denied. User is not a member of this collection."));

        Collection collection = collectionRepository.findCollectionDetailsById(collectionId)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        List<CollectionMemberResponse> memberResponses = collection.getMembers().stream()
                .map(member -> {
                    CollectionMemberResponse response = new CollectionMemberResponse();
                    response.setUserId(member.getUser().getUserId());
                    response.setUsername(member.getUser().getUsername());
                    response.setRole(member.getRole());
                    response.setEmail(member.getUser().getEmail());
                    response.setStatus(member.getStatus());
                    return response;
                }).collect(Collectors.toList());

        List<SimplePaperResponse> paperResponses = collection.getPapers().stream()
                .map(paper -> {
                    SimplePaperResponse response = new SimplePaperResponse();
                    response.setPaperId(paper.getPaperId());
                    response.setTitle(paper.getTitle());
                    return  response;
                }).collect(Collectors.toList());

        CollectionDetailResponse collectionDetailResponse = new CollectionDetailResponse();
        collectionDetailResponse.setCollectionId(collection.getCollectionId());
        collectionDetailResponse.setOwnerUsername(collection.getOwnerUser().getUsername());
        collectionDetailResponse.setName(collection.getName());
        collectionDetailResponse.setMemberCount(memberResponses.size());
        collectionDetailResponse.setPaperCount(paperResponses.size());
        collectionDetailResponse.setPapers(paperResponses);
        collectionDetailResponse.setMembers(memberResponses);

        return collectionDetailResponse;
    }

    @Transactional
    @Override
    public String removePaperFromCollection(int collectionId, int paperId, User user) {
        // 1. Kiểm tra Collection và Paper có tồn tại không
        Collection collection = collectionRepository.findById((long) collectionId)
                .orElseThrow(() -> new RuntimeException("Collection not found.")); // **NÊN dùng ResourceNotFoundException (404)**

        Paper paper = paperRepository.findById((long) paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found.")); // **NÊN dùng ResourceNotFoundException (404)**

        // 2. Kiểm tra quyền (Chỉ Owner hoặc PI mới được xóa paper)
        boolean isOwner = Objects.equals(collection.getOwnerUser().getUserId(), user.getUserId());
        if (!isOwner) {
            collectionMemberRepository
                    .findByCollectionCollectionIdAndUserUserIdAndRole(collectionId, user.getUserId(), CollectionMember.MemberRole.PI)
                    .orElseThrow(() -> new RuntimeException("Permission denied. Only the Collection Owner or PI can remove papers.")); // **NÊN dùng AccessDeniedException (403)**
        }

        // 3. Xóa Paper khỏi Collection
        if (collection.getPapers().remove(paper)) {
            // Lưu Collection sẽ tự động cập nhật bảng liên kết (collection_papers)
            collectionRepository.save(collection);
            return "Paper (ID: " + paperId + ") successfully removed from collection (ID: " + collectionId + ").";
        } else {
            throw new RuntimeException("Paper is not currently in this collection."); // **NÊN dùng BadRequestException (400)**
        }
    }

    @Transactional
    @Override
    public CollectionMember acceptInvitation(int collectionId, User currentUser) {

        CollectionMember member = collectionMemberRepository
                .findByCollectionCollectionIdAndUserUserIdAndStatus(
                        collectionId,
                        currentUser.getUserId(),
                        CollectionMember.JoinStatus.PENDING
                )
                .orElseThrow(() -> new RuntimeException("Invitation not found or already accepted/rejected."));

        member.setStatus(CollectionMember.JoinStatus.JOINED);

        return collectionMemberRepository.save(member);
    }

    @Transactional
    @Override
    public void rejectInvitation(int collectionId, User user) {

        // 1. Tạo ID khóa kết hợp
        CollectionMemberId id = new CollectionMemberId(collectionId, user.getUserId());

        // 2. Tìm bản ghi CollectionMember và kiểm tra người nhận
        CollectionMember member = collectionMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invitation not found for this collection."));

        // 3. Kiểm tra trạng thái phải là PENDING
        if (member.getStatus() != CollectionMember.JoinStatus.PENDING) {
            throw new RuntimeException("Cannot reject. This invitation is not pending. Current status is " + member.getStatus());
        }

        // 4. Kiểm tra người dùng (Đã được kiểm tra ngầm qua ID, nhưng đảm bảo tính bảo mật)
        if (!Objects.equals(member.getUser().getUserId(), user.getUserId())) {
            throw new RuntimeException("Unauthorized access to reject this invitation.");
        }

        // 5. Cập nhật trạng thái thành REJECTED
        member.setStatus(CollectionMember.JoinStatus.REJECTED);
        collectionMemberRepository.save(member);

        // *Lưu ý: Nếu bạn muốn xóa hẳn lời mời bị từ chối khỏi database, hãy dùng:
        // collectionMemberRepository.delete(member);
    }
}
