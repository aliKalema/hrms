package com.smartmax.hrms.entity;

import java.util.List;
import java.util.Set;

import javax.persistence.*;

@Entity
public class Team {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String role;
	private boolean enabled;
	@OneToMany(cascade= CascadeType.MERGE,fetch= FetchType.EAGER)
	@JoinColumn(name="employee_team_id")
	private Set<Employee>employees;
	private int managerId;
	@OneToOne(cascade= CascadeType.MERGE,fetch= FetchType.EAGER)
	private Department department;
	@Transient
	Employee manager;
	public Team(int id, String role, Set<Employee> employees, Department department) {
		super();
		this.id = id;
		this.role = role;
		this.department = department;
		enabled = true;
		this.employees = employees;
	}
	public Team() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public Set<Employee>getEmployees(){
		return employees;
	}
	public void setEmployees(Set<Employee>employees) {
		this.employees = employees;
	}
	public int getManagerId() {
		return managerId;
	}
	public void setManagerId(int managerId) {
		this.managerId = managerId;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled =enabled;
	}
	public Employee getManager() {
		return manager;
	}
	public void setManager(Employee manager) {
		this.manager = manager;
	}
	public void addEmployee(Employee employee){
		this.employees.add(employee);
	}
}
