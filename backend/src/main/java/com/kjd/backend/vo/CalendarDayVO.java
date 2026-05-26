package com.kjd.backend.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CalendarDayVO {
    private Long id;
    private LocalDate tripDate;
    private String weekName;
    private String subsidyCity;
    private boolean mealSelected;
    private boolean transportSelected;
    private boolean phoneSelected;
    private BigDecimal mealStandard;
    private BigDecimal transportStandard;
    private BigDecimal phoneStandard;
    private BigDecimal mealAmount;
    private BigDecimal transportAmount;
    private BigDecimal phoneAmount;
}
