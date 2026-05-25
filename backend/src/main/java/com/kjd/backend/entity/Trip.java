package com.kjd.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("fk_reim_itinerary")
public class Trip {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("main_id")
    private String mainId;

    @TableField("traveler_id")
    private String travelerId;

    @TableField("traveler_no")
    private String travelerNo;

    @TableField("traveler_name")
    private String travelerName;

    @TableField("departure_city_no")
    private String fromCityNo;

    @TableField("departure_city")
    private String fromCityName;

    @TableField("arriving_city_no")
    private String toCityNo;

    @TableField("arriving_city")
    private String toCityName;

    @TableField("departure_date")
    private LocalDate startDate;

    @TableField("arrival_date")
    private LocalDate endDate;

    @TableField("itinerary_instructions")
    private String tripDescription;
}
