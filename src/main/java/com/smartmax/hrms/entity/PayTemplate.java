package com.smartmax.hrms.entity;

import java.util.List;
import java.util.Set;

import javax.persistence.*;

@Entity
public class PayTemplate {
	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private int id;
	private String name;
	private double nssf;
	private double nhif;
	private double grossSalary;
	private double netSalary;
	@OneToMany(cascade= CascadeType.MERGE,fetch= FetchType.EAGER)
	private Set<Employee> employees;
	@OneToMany(cascade= CascadeType.ALL,fetch= FetchType.EAGER)
	List<Payhead>payheads;
	@Transient
	List<Payhead>earnings;
	@Transient
	List<Payhead>deduction;
	@Transient
	private double tax;
	@Transient
	private double taxableIncome;
	@Transient
	private Payslip payslip;
	public PayTemplate(int id, double grossSalary, double netSalary, double nhif, double nssf, String name,Set<Employee>employees, List<Payhead> payheads,String payrollNumber) {
		super();
		this.id = id;
		this.name = name;
		this.employees =employees;
		this.payheads = payheads;
		this.nhif = nhif;
		this.nssf = nssf;
		this.grossSalary = grossSalary;
		this.netSalary =  netSalary;
	}
	public PayTemplate() {
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
	public Set<Employee> getEmployees() {
		return employees;
	}
	public void setEmployees(Set<Employee> employees) {
		this.employees = employees;
	}
	public List<Payhead> getPayheads() {
		return payheads;
	}
	public void setPayheads(List<Payhead> payheads) {
		this.payheads = payheads;
	}
	public List<Payhead> getEarnings() {
		return earnings;
	}
	public void setEarnings(List<Payhead> earnings) {
		this.earnings = earnings;
	}
	public List<Payhead> getDeduction() {
		return deduction;
	}
	public void setDeduction(List<Payhead> deduction) {
		this.deduction = deduction;
	}
	public double getTax() {
		return tax;
	}
	public void setTax(double tax) {
		this.tax = tax;
	}
	public double getNssf() {
		return nssf;
	}
	public void setNssf(double nssf) {
		this.nssf = nssf;
	}
	public double getNhif(){
		return nhif;
	}
	public void setNhif(double nhif){
		this.nhif = nhif;
	}
	public double getGrossSalary(){
		return grossSalary;
	}
	public void setGrossSalary(double grossSalary){
		this.grossSalary = grossSalary;
	}
	public Payslip getPayslip(){
		return payslip;
	}
	public void setPayslip(Payslip payslip){
		this.payslip = payslip;
	}
	public double getNetSalary() {
		return netSalary;
	}
	public void setNetSalary(double netSalary) {
		this.netSalary = netSalary;
	}
	public double getTaxableIncome() {
		return taxableIncome;
	}
	public void setTaxableIncome(double taxableIncome) {
		this.taxableIncome = taxableIncome;
	}
}
