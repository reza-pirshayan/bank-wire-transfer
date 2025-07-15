package com.pirshayan.infrastructure.persistence.achtransferorder.repository;

import com.pirshayan.infrastructure.persistence.achtransferorder.entity.FirstSignatureEntity;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FirstSignatureEntityRepository implements PanacheRepositoryBase<FirstSignatureEntity, String> {
}
