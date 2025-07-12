package com.pirshayan.infrastructure.persistence.achtransferorder.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "secondSignerCandidates")
@NamedQuery(name = "SecondSignerCandidateEntity.findByAchTransferOrderEntityId.list", query = "select s from SecondSignerCandidateEntity as s where s.order_id = :order_id")
public class SecondSignerCandidateEntity extends SignerCandidate {
}
