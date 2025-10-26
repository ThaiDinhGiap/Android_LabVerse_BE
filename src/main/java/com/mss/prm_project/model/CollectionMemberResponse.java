package com.mss.prm_project.model;

import com.mss.prm_project.entity.CollectionMember;
import lombok.Data;

@Data
public class CollectionMemberResponse {
    private int userId;
    private String username;
    private String email;
    private CollectionMember.MemberRole role;
    private CollectionMember.JoinStatus status;
}
