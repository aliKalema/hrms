package com.smartmax.hrms.entity;

import java.time.LocalDate;

import javax.persistence.*;

@Entity
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(columnDefinition = "boolean default true")
	private boolean active;
	private String nationalId;
	private String firstName;
	private String lastName;
	private String otherNames;
	private LocalDate dob;
	private String email;
	private String gender;
	private String phone;
	private LocalDate eoc;
	private LocalDate doj;
	private String employmentType;
	private boolean appo;
	private String nssfPin;
	private String nhifPin;
	private String paymentMode;
	private String accountPin;
	private String kra;
	private String bankName;
	private String branch;
	private String payrollNumber;
	@Column(columnDefinition = "boolean default false")
	private boolean payroll;
	@OneToOne(cascade= CascadeType.ALL,fetch= FetchType.EAGER)
	@JoinColumn(name="image_id")
	private UserImage image;
	@OneToOne(cascade= CascadeType.ALL,fetch= FetchType.EAGER)
	@JoinColumn(name="additional_payment_details_id")
	private AdditionalPaymentDetails additionalPaymentDetails;
	@Transient
	private String jobTitle;
	@Transient
	private String name;
	@Transient
	private String location;
	@Transient
	private  String category;
	@Transient
	private String section;
	@Transient
	private String managerName;
	@Transient
	private String grade;
	@Transient
	private String department;
	@Transient
	private double salary;
	@Transient
	private int imageSet;
	@Transient
	private int age;
	@Transient
	private int experience;

	public Employee() {}
	public Employee(int id, boolean active, String nationalId, String firstName, String lastName, String otherNames, LocalDate dob, String email, String gender, String phone, LocalDate eoc, LocalDate doj, String employmentType, AdditionalPaymentDetails additionalPaymentDetails, boolean appo, String nssfPin, String nhifPin, String paymentMode, String accountPin, String kra, String bankName, String branch, String name, String payrollNumber, boolean payroll, UserImage image, String jobTitle) {
		this.id = id;
		this.active = active;
		this.nationalId = nationalId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.otherNames = otherNames;
		this.dob = dob;
		this.email = email;
		this.gender = gender;
		this.phone = phone;
		this.eoc = eoc;
		this.doj = doj;
		this.employmentType = employmentType;
		this.appo = appo;
		this.nssfPin = nssfPin;
		this.nhifPin = nhifPin;
		this.paymentMode = paymentMode;
		this.accountPin = accountPin;
		this.kra = kra;
		this.bankName = bankName;
		this.branch = branch;
		this.setName(name);
		this.payrollNumber = payrollNumber;
		this.payroll = payroll;
		this.image = image;
		this.jobTitle = jobTitle;
		this.additionalPaymentDetails = additionalPaymentDetails;
	}
	public Employee(UserImage image){
		this.image =image;
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
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getOtherNames() {
		return otherNames;
	}
	public void setOtherNames(String otherNames) {
		this.otherNames = otherNames;
	}
	public LocalDate getDob() {
		return dob;
	}
	public void setDob(LocalDate dob) {
		this.dob = dob;
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
	public LocalDate getDoj() {
		return doj;
	}
	public void setDoj(LocalDate doj) {
		this.doj = doj;
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
	public UserImage getImage() {
		return image;
	}
	public void setImage(UserImage image) {
		this.image = image;
	}
	public String getKra() {
		return kra;
	}
	public void setKra(String kra) {
		this.kra = kra;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public boolean isPayroll() {
		return payroll;
	}
	public void setPayroll(boolean payroll) {
		this.payroll = payroll;
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getPayrollNumber() {
		return payrollNumber;
	}
	public void setPayrollNumber(String payrollNumber) {
		this.payrollNumber = payrollNumber;
	}
	public boolean isActive(){
		return active;
	}
	public void setActive(boolean active){
		this.active = active;
	}
	public void setSalary(double salary){
		this.salary = salary;
	}
	public Double getSalary() {
		return salary;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	public String getJobTitle(){
		return jobTitle;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
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
	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
    public int isImageSet(){
	    return imageSet;
    }
    public void setImageset(int imageSet){
	    this.imageSet= imageSet;
    }
	public int getImageSet() {
		return imageSet;
	}
	public void setImageSet(int imageSet) {
		this.imageSet = imageSet;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getExperience() {
		return experience;
	}
	public void setExperience(int experience) {
		this.experience = experience;
	}
	public AdditionalPaymentDetails getAdditionalPaymentDetails() {
		return additionalPaymentDetails;
	}
	public void setAdditionalPaymentDetails(AdditionalPaymentDetails additionalPaymentDetails) {
		this.additionalPaymentDetails = additionalPaymentDetails;
	}

	@Override
	public String toString() {
		return "Employee{" +
				"id=" + id +
				", active=" + active +
				", nationalId='" + nationalId + '\'' +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", otherNames='" + otherNames + '\'' +
				", dob=" + dob +
				", email='" + email + '\'' +
				", gender='" + gender + '\'' +
				", phone='" + phone + '\'' +
				", eoc=" + eoc +
				", doj=" + doj +
				", employmentType='" + employmentType + '\'' +
				", appo=" + appo +
				", nssfPin='" + nssfPin + '\'' +
				", nhifPin='" + nhifPin + '\'' +
				", paymentMode='" + paymentMode + '\'' +
				", accountPin='" + accountPin + '\'' +
				", kra='" + kra + '\'' +
				", bankName='" + bankName + '\'' +
				", branch='" + branch + '\'' +
				", payrollNumber='" + payrollNumber + '\'' +
				", payroll=" + payroll +
				", image=" + image +
				", jobTitle='" + jobTitle + '\'' +
				", name='" + name + '\'' +
				", location='" + location + '\'' +
				", category='" + category + '\'' +
				", section='" + section + '\'' +
				", managerName='" + managerName + '\'' +
				", grade='" + grade + '\'' +
				", department='" + department + '\'' +
				", salary=" + salary +
				", imageSet=" + imageSet +
				", age=" + age +
				", experience=" + experience +
				'}';
	}
}
