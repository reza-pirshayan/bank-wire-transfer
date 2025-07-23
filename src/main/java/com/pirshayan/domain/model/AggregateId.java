package com.pirshayan.domain.model;

import java.util.Objects;

public abstract class AggregateId<T> {
	private final T id;

	protected AggregateId(T id) {
		if (id == null) {
			throw new IllegalArgumentException("ID value must not be null");
		}
		this.id = id;
	}

	public T getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		AggregateId<?> other = (AggregateId<?>) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
