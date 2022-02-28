package com.smartmax.hrms.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.smartmax.hrms.entity.Payroll;

public interface PayrollRepository extends CrudRepository<Payroll,Integer> {
	@Query(value="SELECT * payroll WHERE MONTH()=? AND YEAR()=?",nativeQuery = true)
	Optional<Payroll>findPayRollByMonthAndYear(int month,int year);

	@Query(value = "SELECT * FROM payroll WHERE payroll.date =?",nativeQuery = true)
	Optional<Payroll> findByPeriod(LocalDate period);

	@Query(value = "INSERT INTO payslip (date,tax,total) VALUES (?,?,?)",nativeQuery=true)
	void insertPayroll(LocalDate date,double tax, double total);

	Optional<Payroll> findByDate(LocalDate date);
}
