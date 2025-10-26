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

    public CollectionMemberResponse() {
    }

    public CollectionMemberResponse(int userId, String username, String email, CollectionMember.MemberRole role, CollectionMember.JoinStatus status) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.status = status;
    }
}
