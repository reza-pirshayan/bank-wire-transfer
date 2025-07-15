package com.pirshayan.infrastructure.persistence.achtransferorder.repository;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pirshayan.infrastructure.persistence.achtransferorder.entity.FirstSignerCandidateEntity;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FirstSignerCandidateEntityRepository implements PanacheRepository<FirstSignerCandidateEntity> {
	public List<FirstSignerCandidateEntity> findByAchTransferOrderEntityId(String achTansferOrderEntityId) {
		String query = "#FirstSignerCandidateEntity.findByAchTransferOrderEntityId:list";
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("order_id", achTansferOrderEntityId);
		return list(query, parameters);
	}
	
	public void deleteByAchTransferOrderEntityId(String achTransferOrderEntityId) {
		String query = "#FirstSignerCandidateEntity.deleteByAchTransferOrderEntityId:void";
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("order_id", achTransferOrderEntityId);
		delete(query, parameters);
	}
	
	public void deleteByAchTransferOrderEntityIds(List<Long> achTransferOrderEntityIds) {
		String query = "#FirstSignerCandidateEntity.deleteByAchTransferOrderEntityIds:void";
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("achTransferOrderEntityIds", achTransferOrderEntityIds);
		delete(query, parameters);
	}
}
