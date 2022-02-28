package com.smartmax.hrms.entity;

import java.util.List;

import javax.persistence.*;

@Entity
public class User {
	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private int id;
	@Column(unique =true,length = 50)
	private String username;
	private String password;
	private boolean enabled;
	private int employeeId;
	@OneToMany(cascade= CascadeType.ALL,fetch= FetchType.EAGER)
	@JoinColumn(name="user_id")
	private List<com.smartmax.hrms.entity.Role>roles;
	@Transient
	private boolean online;
	@Transient
	private Employee employee;
	@Transient
	String mainRole;
	public User(int id, String username, String password,int employeeId, List<Role> roles) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.enabled = true;
		this.roles = roles;
		this.employeeId = employeeId;
	}
	public User() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public String getMainRole(){
		return mainRole;
	}
	public void setMainRole(String mainRole){
		this.mainRole = mainRole;
	}
	public void removePassword(){
		this.password = "";
	}
	public int getEmployeeId(){
		return employeeId;
	}
	public void setEmployeeId(int employeeId){
		this.employeeId = employeeId;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username  + ", roles=" + roles + "]";
	}
}
