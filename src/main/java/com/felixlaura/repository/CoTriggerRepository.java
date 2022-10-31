package com.felixlaura.repository;

import com.felixlaura.entity.CoTriggerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;


public interface CoTriggerRepository extends JpaRepository<CoTriggerEntity, Serializable> {

    public CoTriggerEntity findByCaseNum(Long caseNum);

    List<CoTriggerEntity> findAllByTrgStatus(String trgStatus);
}
