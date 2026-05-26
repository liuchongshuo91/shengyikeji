package com.kjd.backend.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReimbursementVO {
    private Long id;
    private String reimbursementNo;
    private Integer status;
    private String statusName;
    private LocalDateTime creationTime;
    private LocalDate submitDate;
    private String reimbursementTitle;
    private String reimburserId;
    private String reimburserNo;
    private String reimburserName;
    private String reimDepartmentId;
    private String reimDepartmentNo;
    private String reimDepartmentName;
    private String reimCompanyId;
    private String reimCompanyNo;
    private String reimCompanyName;
    private String businessTypeId;
    private String businessTypeNo;
    private String businessTypeName;
    private String businessTripReason;
    private String documentType;
    private BigDecimal subsidyTotal;
    private BigDecimal mealAllowance;
    private BigDecimal transportationAllowance;
    private BigDecimal phoneAllowance;
    private String remarks;
    private List<TripVO> trips = new ArrayList<>();
    private List<SubsidyVO> subsidies = new ArrayList<>();
    private List<AllocationVO> allocations = new ArrayList<>();
}
