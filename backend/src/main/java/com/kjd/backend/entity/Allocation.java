package com.kjd.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Allocation {

    private String id;

    @TableField("reim_company_id")
    private String reimCompanyId;

    @TableField("reim_company_no")
    private String reimCompanyNo;

    @TableField("reim_company_name")
    private String reimCompanyName;

    @TableField("project_id")
    private String projectId;

    @TableField("project_no")
    private String projectNo;

    @TableField("project_name")
    private String projectName;

    @TableField("allocation_ratio")
    private BigDecimal allocationRatio = BigDecimal.ZERO;

    @TableField("allocation_amount")
    private BigDecimal allocationAmount = BigDecimal.ZERO;
}
