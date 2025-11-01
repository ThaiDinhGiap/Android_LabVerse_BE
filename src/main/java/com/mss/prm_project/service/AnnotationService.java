package com.mss.prm_project.service;

import com.mss.prm_project.dto.AnnotationDTO;
import com.mss.prm_project.dto.FileDTO;
import com.mss.prm_project.dto.UserDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface AnnotationService {

    /**
     * Chia sẻ annotation cho 1 user cụ thể
     * @return AnnotationDTO sau khi đã thêm reader
     */
    List<UserDTO> shareAnnotationToOther(long paperId, List<Long> userIdList);

    /**
     * Gỡ quyền đọc annotation khỏi user
     * @return AnnotationDTO sau khi đã cập nhật danh sách readers
     */
    List<UserDTO> removeReaderFromAnnotation(long annotationId, long userId);

    /**
     * Lấy tất cả annotation mà user có quyền đọc tương ứng với một Paper cụ thể
     */
    List<AnnotationDTO> findAllReadableAnnotationByUserId(int collectionId, long paperId);

    /**
     * Lấy thông tin chi tiết của annotation theo id
     */
    AnnotationDTO findAnnotationById(long annotationId);

    /**
     * Tạo annotation mới
     * @return AnnotationDTO đã được tạo (có id)
     */
    AnnotationDTO createAnnotation(String annotationName, MultipartFile file, int paperId, int userId) throws IOException;

    /**
     * Xóa annotation
     * @return true nếu xóa thành công, false nếu không tồn tại
     */
    boolean deleteAnnotation(long annotationId);

    /**
     * Xóa annotation
     * @return true nếu xóa thành công, false nếu không tồn tại
     */
    AnnotationDTO importAnnotationFromOtherMember(long annotationId, int paperId);


}
