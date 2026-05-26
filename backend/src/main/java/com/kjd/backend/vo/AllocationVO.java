package com.kjd.backend.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AllocationVO {
    private String id;
    private String reimCompanyId;
    private String reimCompanyNo;
    private String reimCompanyName;
    private String projectId;
    private String projectNo;
    private String projectName;
    private BigDecimal allocationRatio;
    private BigDecimal allocationAmount;
}
