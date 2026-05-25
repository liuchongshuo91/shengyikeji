package com.kjd.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("fk_subsidy_calendar")
public class CalendarDay {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("main_id")
    private String mainId;

    @TableField("travel_date")
    private LocalDate tripDate;

    @TableField("travel_date_week")
    private String weekName;

    @TableField("subsidized_cities")
    private String subsidyCity;

    @TableField("is_reimbursed")
    private String reimbursed;

    @TableField(exist = false)
    private boolean mealSelected;

    @TableField(exist = false)
    private boolean transportSelected;

    @TableField(exist = false)
    private boolean phoneSelected;

    @TableField("standard_meal_expenses_amount")
    private BigDecimal mealStandard = BigDecimal.ZERO;

    @TableField("standard_traffic_amount")
    private BigDecimal transportStandard = new BigDecimal("40.00");

    @TableField("standard_communication_amount")
    private BigDecimal phoneStandard = new BigDecimal("40.00");

    @TableField("meal_expenses_amount")
    private BigDecimal mealAmount = BigDecimal.ZERO;

    @TableField("traffic_amount")
    private BigDecimal transportAmount = BigDecimal.ZERO;

    @TableField("communication_amount")
    private BigDecimal phoneAmount = BigDecimal.ZERO;
}
