package com.company.app.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 系统用户
 * @author YunJ
 */
@Data
public class User {
    @TableId
    private String id;

    private String username;

    private String password;

    private String userRole;
}
