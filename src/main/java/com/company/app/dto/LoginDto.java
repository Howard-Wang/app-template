package com.company.app.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 用户登录传入的 dto
 * @author YunJ
 */
@Data
public class LoginDto {

   @NotNull
   @Size(min = 1, max = 50)
   private String username;

   @NotNull
   @Size(min = 6, max = 100)
   private String password;

   private Boolean rememberMe;
}
