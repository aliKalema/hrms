package com.smartmax.hrms.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity
public class Payslip {
	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private int id;
	private LocalDate payPeriod;
	private int payDays;
	@OneToOne(cascade= CascadeType.MERGE,fetch= FetchType.EAGER)
	private Employee employee;
	private double tax;
	private double nssf;
	private double nhif;
	private double grossSalary;
	private double netSalary;
	private double netTax;
	@OneToMany(cascade= CascadeType.ALL,fetch= FetchType.EAGER)
	private Set<Relief> reliefs;
	@OneToMany(cascade= CascadeType.ALL,fetch= FetchType.EAGER)
	List<Payhead>payheads;
	@Transient
	List<Payhead>earnings;
	@Transient
	List<Payhead> deductions;
	@Transient
	String templateName;
	@Transient
	String periodString;
	@Transient
	double totalDeductions;
	@Transient
	double taxableIncome;
	public Payslip(int id, LocalDate payPeriod,int payDays, Employee employee, double tax, double nssf, double nhif, double grossSalary, double netSalary, double netTax, Set<Relief> reliefs, List<Payhead> payheads, List<Payhead> earnings, List<Payhead> deductions, String templateName) {
		this.id = id;
		this.payPeriod = payPeriod;
		this.payDays = payDays;
		this.employee = employee;
		this.tax = tax;
		this.nssf = nssf;
		this.nhif = nhif;
		this.grossSalary = grossSalary;
		this.netSalary = netSalary;
		this.netTax = netTax;
		this.reliefs = reliefs;
		this.payheads = payheads;
		this.earnings = earnings;
		this.deductions = deductions;
		this.templateName = templateName;
	}

	public Payslip() {}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
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
	public List<Payhead> getDeductions() {
		return deductions;
	}
	public void setDeductions(List<Payhead> deductions) {
		this.deductions = deductions;
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
	public double getGrossSalary() {
		return grossSalary;
	}
	public void setGrossSalary(double grossSalary) {
		this.grossSalary = grossSalary;
	}
	public Set<Relief>getReliefs(){
		return reliefs;
	}
	public void setReliefs(Set<Relief>reliefs){
		this.reliefs = reliefs;
	}
	public double getNetSalary(){
		return netSalary;
	}
	public void setNetSalary(double netSalary){
		this.netSalary = netSalary;
	}
	public double getNetTax(){
		return netTax;
	}
    public void setNetTax(double netTax){
		this.netTax = netTax;
	}
	public double getNhif(){
		return nhif;
	}
	public void setNhif(double nhif){
		this.nhif = nhif;
	}
	public LocalDate getPayPeriod(){
		return payPeriod;
	}
	public void setPayPeriod(LocalDate payPeriod){
		this.payPeriod = payPeriod;
	}
	public int payDays(){
		return payDays;
	}
	public void setPayDays(int payDays){
		this.payDays = payDays;
	}
	public String getTemplateName(){
		return templateName;
	}
	public double getTotalDeduction(){
		return totalDeductions;
	}
	public void setTotalDeduction(double totalDeductions){
		this.totalDeductions = totalDeductions;
	}
	public void setTemplateName(String templateName){
		this.templateName = templateName;
	}
	public int getPayDays() {
		return payDays;
	}
	public double getTotalDeductions() {
		return totalDeductions;
	}
	public void setTotalDeductions(double totalDeductions) {
		this.totalDeductions = totalDeductions;
	}
	public double getTaxableIncome() {
		return taxableIncome;
	}
	public void setTaxableIncome(double taxableIncome) {
		this.taxableIncome = taxableIncome;
	}
	public String getPeriodString() {
		return periodString;
	}
	public void setPeriodString(String periodString) {
		this.periodString = periodString;
	}

	@Override
	public String toString() {
		return "Payslip{" +
				"payPeriod=" + payPeriod +
				", payDays=" + payDays +
				", employee=" + employee +
				", tax=" + tax +
				", nssf=" + nssf +
				", nhif=" + nhif +
				", grossSalary=" + grossSalary +
				", netSalary=" + netSalary +
				", netTax=" + netTax +
				'}';
	}
}
