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
		String query = "#SecondSignerCandidateEntity.findByAchTransferOrderEntityId.list";
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("order_id", achTransferOrderEntityId);
		return list(query, parameters);
	}
	
	public void deleteByAchTransferOrderEntityId(String achTransferOrderEntityId) {
		String query = "#SecondSignerCandidateEntity.deleteByAchTransferOrderEntityId:void";
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("order_id", achTransferOrderEntityId);
		delete(query,parameters);
	}
	
	public void deleteByAchTransferOrderEntityIds(List<Long> achTransferOrderEntityIds) {
		String query = "#SecondSignerCandidateEntity.deleteByAchTransferOrderEntityIds:void";
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("achTransferOrderEntityIds", achTransferOrderEntityIds);
		delete(query, parameters);
	}
}
