package com.smartmax.hrms.entity;

import java.time.LocalDate;


public class EmployeeResponse {
	private int id;
	private String name;
	private String nationalId;
	private String email;
	private String gender;
	private String phone;
	private LocalDate eoc;
	private String employmentType;
	private boolean appo;
	private String nssfPin;
	private String nhifPin;
	private String paymentMode;
	private String accountPin;
	private String kra;
	private String location;
	private String age;
	private int experience;
	private String jobTitle;
	private String department;
	private String manager;
	private String category;
	private String section;
	private String grade;
	private String image;
	public EmployeeResponse(int id, String nationalId, String firstName, String lastName, String otherNames,
			LocalDate dob, String email, String gender, String phone, LocalDate eoc, LocalDate doj,
			String employmentType, boolean appo, String nssfPin, String nhifPin, String paymentMode, String accountPin,
			String kra, String image, String location, String age, int experience, String jobTitle,
			String department, String manager, String category, String section, String grade, String name, User user) {
		super();
		this.id = id;
		this.nationalId = nationalId;
		this.email = email;
		this.gender = gender;
		this.phone = phone;
		this.eoc = eoc;
		this.employmentType = employmentType;
		this.appo = appo;
		this.nssfPin = nssfPin;
		this.nhifPin = nhifPin;
		this.paymentMode = paymentMode;
		this.accountPin = accountPin;
		this.kra = kra;
		this.image = image;
		this.location = location;
		this.age = age;
		this.experience = experience;
		this.jobTitle = jobTitle;
		this.department = department;
		this.manager = manager;
		this.category = category;
		this.section = section;
		this.grade = grade;
		this.name = name;
	}
	public EmployeeResponse() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNationalId() {
		return nationalId;
	}
	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public LocalDate getEoc() {
		return eoc;
	}
	public void setEoc(LocalDate eoc) {
		this.eoc = eoc;
	}
	public String getEmploymentType() {
		return employmentType;
	}
	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}
	public boolean isAppo() {
		return appo;
	}
	public void setAppo(boolean appo) {
		this.appo = appo;
	}
	public String getNssfPin() {
		return nssfPin;
	}
	public void setNssfPin(String nssfPin) {
		this.nssfPin = nssfPin;
	}
	public String getNhifPin() {
		return nhifPin;
	}
	public void setNhifPin(String nhifPin) {
		this.nhifPin = nhifPin;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public String getAccountPin() {
		return accountPin;
	}
	public void setAccountPin(String accountPin) {
		this.accountPin = accountPin;
	}
	public String getKra() {
		return kra;
	}
	public void setKra(String kra) {
		this.kra = kra;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public int getExperience() {
		return experience;
	}
	public void setExperience(int experience) {
		this.experience = experience;
	}
	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
