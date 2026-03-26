package cn.bugstack.types.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordRequest {

    @NotBlank(message = "id 不能为空")
    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;

}