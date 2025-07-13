package com.pirshayan.infrastructure.persistence.financeofficerrule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "positions")
public class PositionEntity {
	@Id
	@Column(name = "id", columnDefinition = "NUMBER")
	private Integer id;

	@Column(name = "title", length = 30, nullable = false)
	private String title;

	public PositionEntity() {
	}

	public PositionEntity(Integer id, String title) {
		this.id = id;
		this.title = title;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
