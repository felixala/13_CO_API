package com.felixlaura.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;


@Data
@Entity
@Table(name = "ELIBILITY_DTLS")
public class EligDtlsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer edTradeId;

    private Long caseNum;

    private String holderName;

    private Long holderSsn;

    private String planName;

    private String planStatus;

    private LocalDate planStartDate;

    private LocalDate planEndDate;

    private Double benefitAmt;

    private String denialReason;

}
