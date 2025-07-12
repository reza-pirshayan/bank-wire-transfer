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
		String QUERY = "#FirstSignerCandidateEntity.findByAchTransferOrderEntityId:list";
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("order_id", achTansferOrderEntityId);
		return list(QUERY, parameters);
	}
	
	public void deleteByAchTransferOrderEntityId(String achTransferOrderEntityId) {
		String QUERY = "#FirstSignerCandidateEntity.deleteByAchTransferOrderEntityId:void";
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("order_id", achTransferOrderEntityId);
		delete(QUERY, parameters);
	}
}
