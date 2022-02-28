package com.smartmax.hrms.service;

import com.smartmax.hrms.entity.*;
import com.smartmax.hrms.repository.*;
import com.smartmax.hrms.utils.SystemUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.util.*;

@Service
public class PayrollService {
    @Autowired
    PayTemplateRepository payTemplateRepository;

    @Autowired
    TaxRepository taxRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    PayrollRepository payrollRepository;

    @Autowired
    PayslipRepository payslipRepository;

    @Autowired
    EmployeeService employeeService;

    public List<Payslip> getPayrollPayslips(List<Employee> employees,LocalDate period){
        List<Payslip>payslips = new ArrayList<>();
        for(Employee  employee : employees){
            Payslip payslip = calculateEmployeePayslip(employee,period);
            payslips.add(payslip);
        }
        return payslips;
    }

    public Payslip calculateEmployeePayslip(Employee employee,LocalDate period){
        PayTemplate payTemplate = payTemplateRepository.findByEmployeeId(employee.getId()).get();
        List<Payhead>earnings = new ArrayList<>();
        List<Payhead>deductions = new ArrayList<>();
        employeeService.setStructure(employee);
        double totalDeductions = 0;
        double totalEarnings = 0;
        Payslip payslip = new Payslip();
        List<Relief>employeeReliefs = new ArrayList<>(calculateReliefs(employee));
        for(Payhead payhead : payTemplate.getPayheads()){
            if(payhead.getType().equalsIgnoreCase("earning")){
                earnings.add(payhead);
                totalEarnings = totalEarnings + payhead.getAmount();
            }
            else if(payhead.getType().equalsIgnoreCase("deduction")){
                deductions.add(payhead);
                totalDeductions = totalDeductions + payhead.getAmount();
            }
        }
        AdditionalPaymentDetails additionalPaymentDetails = employee.getAdditionalPaymentDetails();
        if(additionalPaymentDetails != null) {
            Set<ExtraPayhead> extraPayheads = additionalPaymentDetails.getExtraPayheads();
            if (extraPayheads.size() > 0) {
                for (ExtraPayhead ep : extraPayheads) {
                    if (ep.isAlways()) {
                        Payhead alwaysPayhead = new Payhead();
                        alwaysPayhead.setName(ep.getName());
                        alwaysPayhead.setAmount(ep.getAmount());
                        alwaysPayhead.setType(ep.getType());
                        if (alwaysPayhead.getType().equalsIgnoreCase("earning")) {
                            earnings.add(alwaysPayhead);
                            totalEarnings = totalEarnings + alwaysPayhead.getAmount();
                        } else if (alwaysPayhead.getType().equalsIgnoreCase("deduction")) {
                            deductions.add(alwaysPayhead);
                            totalDeductions = totalDeductions + alwaysPayhead.getAmount();
                        }
                    }
                    else {
                        if (period.equals(ep.getStartDate()) ||
                            period.equals(ep.getEndDate()) ||
                            (period.isAfter(ep.getStartDate()) && period.isBefore(ep.getEndDate()))) {
                            Payhead periodicPayhead = new Payhead();
                            periodicPayhead.setName(ep.getName());
                            periodicPayhead.setAmount(ep.getAmount());
                            periodicPayhead.setType(ep.getType());
                            if (periodicPayhead.getType().equalsIgnoreCase("earning")) {
                                earnings.add(periodicPayhead);
                                totalEarnings = totalEarnings + periodicPayhead.getAmount();
                            } else if (periodicPayhead.getType().equalsIgnoreCase("deduction")) {
                                deductions.add(periodicPayhead);
                                totalDeductions = totalDeductions + periodicPayhead.getAmount();
                            }
                        }
                    }
                }
            }
        }
        totalDeductions = totalDeductions + payTemplate.getNhif();
        payslip.setReliefs(new HashSet<>(employeeReliefs));
        payslip.setEmployee(employee);
        payslip.setEarnings(earnings);
        payslip.setDeductions(deductions);
        payslip.setId(employee.getId());
        payslip.setNssf(payTemplate.getNssf());
        payslip.setNhif(payTemplate.getNhif());
        payslip.setGrossSalary(totalEarnings-payslip.getNssf());
        calculateNetTax(payslip,employeeReliefs);
        payslip.setTotalDeduction(totalDeductions);
        payslip.setNetSalary((payslip.getGrossSalary() - (payslip.getNetTax() + payslip.getTotalDeduction())));
        return payslip;
    }

    public Set<Relief>calculateReliefs(Employee employee){
        Set<Relief>reliefs = new HashSet<>();
        List<Tax>taxes = new ArrayList<>();
        taxRepository.findAll().forEach(taxes :: add);
        for(Relief relief :taxes.get(0).getReliefs()){
            if(relief.isEveryOne()){
                reliefs.add(relief);
            }
        }
        AdditionalPaymentDetails additionalPaymentDetails = employee.getAdditionalPaymentDetails();
        if(additionalPaymentDetails != null) {
            if(additionalPaymentDetails.getReliefs().size()>0){
                reliefs.addAll(additionalPaymentDetails.getReliefs());
            }
        }
        return reliefs;
    }

    public void calculateNetTax(Payslip payslip,List<Relief>reliefs){
        List<Tax>taxes = new ArrayList<>();
        taxRepository.findAll().forEach(taxes::add);
        Tax tax = taxes.get(0);
        double grossTax = tax.calculate(payslip.getGrossSalary());
        payslip.setTax(grossTax);
        double totalReliefs = 0;
        for(Relief relief : reliefs){totalReliefs = totalReliefs + relief.getRate();}
        if((grossTax - totalReliefs)<0){ payslip.setNetTax(0);}
        else{payslip.setNetTax((grossTax - totalReliefs));}
    }

    public String testTax(double grossSalary){
        List<Tax>taxes = new ArrayList<>();
        taxRepository.findAll().forEach(taxes::add);
        Tax tax = taxes.get(0);
        double grossTax =  tax.calculate(grossSalary);
        double reliefs = 0;
        for(Relief relief : tax.getReliefs()){
            if(relief.isEveryOne()){
                reliefs = reliefs + relief.getRate();
            }
        }
        double netTax =  grossTax- reliefs;
        if(netTax<0){
            return "0";
        }
        return String.valueOf(netTax);
    }

    public void createPayslipOnPayTemplate(PayTemplate template){
        List<Payhead>earnings = new ArrayList<>();
        List<Payhead>deductions = new ArrayList<>();
        double totalDeduction = template.getNhif();
        Payslip slip = new Payslip();
        for(Payhead payhead: template.getPayheads()){
            if(payhead.getType().equals("earning")){
                earnings.add(payhead);
            }
            if(payhead.getType().equals("deduction")){
                deductions.add(payhead);
                totalDeduction =  totalDeduction + payhead.getAmount();
            }
        }
        Set<Relief> reliefs = new HashSet<>();
        List<Tax>taxes = new ArrayList<>();
        taxRepository.findAll().forEach(taxes :: add);
        double tax =  taxes.get(0).calculate(template.getGrossSalary()-template.getNssf());
        double totalReliefs =  0;
        slip.setTax(tax);
        for(Relief relief :taxes.get(0).getReliefs()){
            if(relief.isEveryOne()){
                reliefs.add(relief);
                totalReliefs = totalReliefs + relief.getRate();
            }
        }
        double netTax =  Math.round((tax-totalReliefs)* 100.0) / 100.0;
        if(netTax<0){
            netTax = 0;
        }
        slip.setNetTax(netTax);
        slip.setReliefs(reliefs);
        slip.setTaxableIncome(template.getGrossSalary()- template.getNssf());
        slip.setTotalDeduction(totalDeduction);
        slip.setEarnings(earnings);
        slip.setDeductions(deductions);
        slip.setGrossSalary(template.getGrossSalary());
        slip.setNetSalary(template.getGrossSalary()-(totalDeduction+netTax));
        slip.setNhif(template.getNhif());
        slip.setNssf(template.getNssf());
        template.setPayslip(slip);
    }

    public ByteArrayOutputStream createP10Csv(Payroll payroll ){
        ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream();
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
            for(Payslip payslip : payroll.getPayslips()){
                Employee employee = payslip.getEmployee();
                SystemUtils.getName(employee);
                List<String>csv =Arrays.asList(
                                              employee.getKra(),
                                              employee.getName(),
                                              "Resident",
                                              "Primary Employee",
                                              String.valueOf(payslip.getGrossSalary()+payslip.getNssf()),
                                              "0","0","0","0","0","0","0","","0","0","","0",
                                              "Benefit not given",
                                              "","","","","","",
                                              String.valueOf(payslip.getNssf()),
                                              "","0","0","","","",
                                              "2400",
                                              "0","",
                                              String.valueOf(payslip.getNetTax())
                                              );
                csvPrinter.printRecord(csv);
            }
            csvPrinter.flush();
            writer.close();
        } catch (IOException e) {e.printStackTrace();}
        return byteArrayOutputStream;
    }

    public Payslip calculateTax(Payslip payslip){
        List<Tax>taxes = new ArrayList<>();
        taxRepository.findAll().forEach(taxes::add);
        Tax tax = taxes.get(0);
        double grossTax =  tax.calculate(payslip.getGrossSalary());
        payslip.setTax(grossTax);
        double reliefAmount = 0;
        List<Relief> everyOne = new ArrayList<>(tax.getReliefs());
        for(Relief relief : everyOne){
            if(relief.isEveryOne()){
                reliefAmount = reliefAmount + relief.getRate();
            }
        }
        if((grossTax - reliefAmount)<0){
            payslip.setNetTax(0);
        }
        else{
            payslip.setNetTax((grossTax - reliefAmount));
        }
        return payslip;
    }
}
