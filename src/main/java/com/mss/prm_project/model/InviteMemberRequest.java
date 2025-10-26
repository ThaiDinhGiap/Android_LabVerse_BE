package com.mss.prm_project.model;

import lombok.Data;
import lombok.NonNull;

public class InviteMemberRequest {

    private String invitedUserEmail;

    public InviteMemberRequest() {
    }

    public InviteMemberRequest(String invitedUserEmail) {
        this.invitedUserEmail = invitedUserEmail;
    }

    public String getInvitedUserEmail() {
        return invitedUserEmail;
    }

    public void setInvitedUserEmail(String invitedUserEmail) {
        this.invitedUserEmail = invitedUserEmail;
    }
}
