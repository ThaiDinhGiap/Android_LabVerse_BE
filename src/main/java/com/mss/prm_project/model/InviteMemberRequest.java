package com.mss.prm_project.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class InviteMemberRequest {

    @NonNull
    private String invitedUserEmail;
}
