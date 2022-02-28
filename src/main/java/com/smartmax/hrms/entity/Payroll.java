package com.smartmax.hrms.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.*;

@Entity
public class Payroll {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private LocalDate date;
	private double total;
	private double tax;
	private double nssfTotal;
	private double nhifTotal;
	private double netSalaryTotal;
	private double grossSalaryTotal;
	private int generalId;
	@Transient
	private String dateString;
	@Transient
	private String nssf;
	@Transient
	private String  nhif;
	@Transient
	private String totalString;
	@OneToMany(cascade= CascadeType.ALL,fetch= FetchType.EAGER)
	List<Payslip>payslips;
	public Payroll(int id, LocalDate date, double total, double tax, List<Payslip> payslips) {
		this.id = id;
		this.date = date;
		this.total = total;
		this.tax = tax;
		this.payslips = payslips;
	}
	public Payroll() {}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public List<Payslip> getPayslips() {
		return payslips;
	}
	public void setPayslips(List<Payslip> payslips) {
		this.payslips = payslips;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public double getTax() {
		return tax;
	}
	public void setTax(double tax) {
		this.tax = tax;
	}
	public String getDateString() {
		return dateString;
	}
	public void setDateString(String dateString) {
		this.dateString = dateString;
	}
	public String getNssf() {
		return nssf;
	}
	public void setNssf(String nssf) {
		this.nssf = nssf;
	}
	public String getNhif() {
		return nhif;
	}
	public void setNhif(String nhif) {
		this.nhif = nhif;
	}
	public int getGeneralId() {
		return generalId;
	}
	public void setGeneralId(int generalId) {
		this.generalId = generalId;
	}
	public String getTotalString() {
		return totalString;
	}
	public void setTotalString(String totalString) {
		this.totalString = totalString;
	}
	public double getNssfTotal() {
		return nssfTotal;
	}
	public void setNssfTotal(double nssfTotal) {
		this.nssfTotal = nssfTotal;
	}
	public double getNhifTotal() {
		return nhifTotal;
	}
	public void setNhifTotal(double nhifTotal) {
		this.nhifTotal = nhifTotal;
	}
	public double getNetSalaryTotal() {
		return netSalaryTotal;
	}
	public double getGrossSalaryTotal() {
		return grossSalaryTotal;
	}
	public void setGrossSalaryTotal(double grossSalaryTotal) {
		this.grossSalaryTotal = grossSalaryTotal;
	}
	public void setNetSalaryTotal(double netSalaryTotal) {
		this.netSalaryTotal = netSalaryTotal;
	}
}
