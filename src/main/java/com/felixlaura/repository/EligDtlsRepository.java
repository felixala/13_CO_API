package com.felixlaura.repository;

import com.felixlaura.entity.CoTriggerEntity;
import com.felixlaura.entity.EligDtlsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface EligDtlsRepository extends JpaRepository<EligDtlsEntity, Serializable> {

    EligDtlsEntity findByCaseNum(Long caseNum);

}
