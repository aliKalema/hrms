package com.smartmax.hrms.repository;

import com.smartmax.hrms.entity.Payslip;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PayslipRepository extends CrudRepository<Payslip,Integer> {
    @Query(value = "SELECT * FROM payslip WHERE payslip.id=?",nativeQuery=true)
    Optional<Payslip> findById(int id);

    @Query(value = "SELECT * FROM payslip WHERE payslip.employee_id = ? AND payslip.pay_period = ?",nativeQuery = true)
    Optional<Payslip>findByEmployeeIdAndpayPeriod(int id, LocalDate date);

    List<Payslip> findByEmployeeId(int id);
}
