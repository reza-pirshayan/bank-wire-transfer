package com.pirshayan.infrastructure.persistence.achtransferorder.repository;

import com.pirshayan.infrastructure.persistence.achtransferorder.entity.SecondSignatureEntity;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SecondSignatureEntityRepository implements PanacheRepositoryBase<SecondSignatureEntity, String> {

}
