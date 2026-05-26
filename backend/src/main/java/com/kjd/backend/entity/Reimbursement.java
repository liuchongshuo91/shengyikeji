package com.kjd.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName(value = "fk_reim_main", autoResultMap = true)
public class Reimbursement {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("reim_no")
    private String reimbursementNo;

    @TableField("status")
    private Integer status = 0;

    @Version
    private Integer version = 0;

    @TableField(exist = false)
    private String statusName = "草稿";

    @TableField("creation_time")
    private LocalDateTime creationTime;

    @TableField("submit_date")
    private LocalDate submitDate;

    @TableField("reimbursement_title")
    private String reimbursementTitle;

    @TableField("reimburser_id")
    private String reimburserId;

    @TableField("reimburser_no")
    private String reimburserNo;

    @TableField("reimburser_name")
    private String reimburserName;

    @TableField("reim_department_id")
    private String reimDepartmentId;

    @TableField("reim_department_no")
    private String reimDepartmentNo;

    @TableField("reim_department_name")
    private String reimDepartmentName;

    @TableField("reim_company_id")
    private String reimCompanyId;

    @TableField("reim_company_no")
    private String reimCompanyNo;

    @TableField("reim_company_name")
    private String reimCompanyName;

    @TableField("business_type_id")
    private String businessTypeId;

    @TableField("business_type_no")
    private String businessTypeNo;

    @TableField("business_type_name")
    private String businessTypeName;

    @TableField("business_trip_reason")
    private String businessTripReason;

    @TableField("document_type")
    private String documentType = "日常报销单";

    @TableField("subsidy_total")
    private BigDecimal subsidyTotal = BigDecimal.ZERO;

    @TableField("meal_allowance")
    private BigDecimal mealAllowance = BigDecimal.ZERO;

    @TableField("transportation_allowance")
    private BigDecimal transportationAllowance = BigDecimal.ZERO;

    @TableField("phone_allowance")
    private BigDecimal phoneAllowance = BigDecimal.ZERO;

    @TableField("remarks")
    private String remarks;

    @TableField(exist = false)
    private List<Trip> trips = new ArrayList<>();

    @TableField(exist = false)
    private List<Subsidy> subsidies = new ArrayList<>();

    @TableField(value = "allocations_json", typeHandler = JacksonTypeHandler.class)
    private List<Allocation> allocations = new ArrayList<>();
}
