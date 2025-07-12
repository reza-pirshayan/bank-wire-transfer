package com.pirshayan.infrastructure.persistence.achtransferorder.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pirshayan.infrastructure.persistence.achtransferorder.entity.SecondSignerCandidateEntity;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SecondSignerCandidateEntityRepository implements PanacheRepository<SecondSignerCandidateEntity> {
	public List<SecondSignerCandidateEntity> findByAchTransferOrderEntityId(String achTransferOrderEntityId) {
		String QUERY = "#SecondSignerCandidateEntity.findByAchTransferOrderEntityId.list";
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("order_id", achTransferOrderEntityId);
		return list(QUERY, parameters);
	}
	
	public void deleteByAchTransferOrderEntityId(String achTransferOrderEntityId) {
		String QUERY = "#SecondSignerCandidateEntity.deleteByAchTransferOrderEntityId:void";
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("order_id?", achTransferOrderEntityId);
		delete(QUERY,parameters);
	}
}
