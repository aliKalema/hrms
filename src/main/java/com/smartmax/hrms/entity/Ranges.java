package com.smartmax.hrms.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Ranges {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;
	int position;
	private double band;
	private double rate;
	public Ranges(int id, int position, double band, double rate) {
		super();
		this.id = id;
		this.position = position;
		this.band = band;
		this.rate = rate;
	}
	public Ranges() {
		super();
	}
	public double getBand() {
		return band;
	}
	public void setBand(double band) {
		this.band = band;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position =position;
	}
	@Override
	public String toString() {
		return String.valueOf(band) +" : " +String.valueOf(rate);
	}
	
}
