package com.kjd.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName("fk_reim_subsidy")
public class Subsidy {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("main_id")
    private String mainId;

    @TableField("trip_id")
    private String tripId;

    @TableField("traveler_id")
    private String travelerId;

    @TableField("traveler_name")
    private String travelerName;

    @TableField(exist = false)
    private String tripDateRange;

    @TableField("subsidy_days")
    private Integer days = 0;

    @TableField(exist = false)
    private String route;

    @TableField("arriving_city")
    private String subsidyCity;

    @TableField("application_amount")
    private BigDecimal applyAmount = BigDecimal.ZERO;

    @TableField("subsidy_amount")
    private BigDecimal subsidyAmount = BigDecimal.ZERO;

    @TableField(exist = false)
    private List<CalendarDay> calendar = new ArrayList<>();
}
