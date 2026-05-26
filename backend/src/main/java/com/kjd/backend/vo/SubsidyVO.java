package com.kjd.backend.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class SubsidyVO {
    private Long id;
    private Long mainId;
    private Long tripId;
    private String travelerId;
    private String travelerName;
    private String tripDateRange;
    private Integer days;
    private String route;
    private String subsidyCity;
    private BigDecimal applyAmount;
    private BigDecimal subsidyAmount;
    private List<CalendarDayVO> calendar = new ArrayList<>();
}
