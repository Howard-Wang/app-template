package com.company.app.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 配置信息
 * @author YunJ
 */
@Data
public class Setting {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String value;
}
