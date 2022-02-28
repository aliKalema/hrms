package com.smartmax.hrms.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Relief {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;
	private String name;
	private double rate;
	private boolean everyOne;
	@OneToMany(cascade= CascadeType.ALL,fetch= FetchType.EAGER)
	private List<Employee> employees;
	public Relief(int id, String name, double rate, boolean everyOne, List<Employee> employees) {
		this.id = id;
		this.name = name;
		this.rate = rate;
		this.everyOne = everyOne;
		this.employees = employees;
	}

	public Relief() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	public boolean isEveryOne() {
		return everyOne;
	}
	public void setEveryOne(boolean everyOne) {
		this.everyOne = everyOne;
	}
	public List<Employee> getEmployees() {
		return employees;
	}
	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}


}
