package kz.hackathon.secretsantaapp.dto.resetPassword;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    private String newPassword;
    private String confirmPassword;
}
