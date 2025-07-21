package com.pirshayan.domain.model.achtransferorder;

import com.pirshayan.domain.model.AggregateId;
import com.pirshayan.domain.model.Validator;

public class AchTransferOrderId extends AggregateId<String> {
	public AchTransferOrderId(String id) {
		super(Validator.validateAchTransferOrderId(id));
	}

}
