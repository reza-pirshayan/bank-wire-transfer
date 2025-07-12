package com.pirshayan.infrastructure.persistence.achtransferorder.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "firstSignerCandidates")
@NamedQuery(name = "FirstSignerCandidateEntity.findByAchTransferOrderEntityId:list", query = "select f from FirstSignerCandidateEntity as f where f.order_id = :order_id")
public class FirstSignerCandidateEntity extends SignerCandidate {
}
