package com.mss.prm_project.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class UserDTO extends BaseDTO implements Serializable {
    private String fullName;
    private String email;
    private String username;
    private Boolean isEnabled;
    private Long role;
    private LocalDateTime emailVerifyAt;
    private boolean pushNotification;
    private boolean emailNotification;
}
