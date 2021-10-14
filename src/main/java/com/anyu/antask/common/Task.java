package com.anyu.antask.common;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/21
 */
@Data
@TableName
public class Task {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    @TableField(fill = FieldFill.INSERT)
    private LocalDate lastExcTime;

    @TableField(fill = FieldFill.INSERT)
    private TaskStatus taskStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
}
