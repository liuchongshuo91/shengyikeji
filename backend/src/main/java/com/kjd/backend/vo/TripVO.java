package com.kjd.backend.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TripVO {
    private Long id;
    private Long mainId;
    private String travelerId;
    private String travelerNo;
    private String travelerName;
    private String fromCityNo;
    private String fromCityName;
    private String toCityNo;
    private String toCityName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String tripDescription;
}
