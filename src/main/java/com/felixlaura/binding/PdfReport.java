package com.felixlaura.binding;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PdfReport {

    private String holderName;

    private String planName;

    private String planStatus;

    private LocalDate planStartDate;

    private LocalDate planEndDate;

    private Double benefitAmt;

    private String denialReason;


}
