package com.smartmax.hrms.controllers;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.smartmax.hrms.entity.*;
import com.smartmax.hrms.repository.*;
import com.smartmax.hrms.service.EmployeeService;
import com.smartmax.hrms.service.PayrollService;
import com.smartmax.hrms.utils.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
public class HRcontroller {
Logger logger =  LoggerFactory.getLogger(HRcontroller.class);
	@Autowired
	PayHeadRepository payHeadRepository;
	
	@Autowired
	TaxRepository taxRepository;
	
	@Autowired
	ReliefRepository reliefRepository;

	@Autowired
	PayTemplateRepository payTemplateRepository;

	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	PayrollRepository payrollRepository;

	@Autowired
	PayrollService payrollService;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	GeneralRepository generalRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Autowired
	ReminderRepository reminderRepository;

	@Autowired
	ExtraPayheadRepository extraPayheadRepository;

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handle(Exception e) {
		logger.warn("Returning HTTP 400 Bad Request", e);
	}

	@GetMapping("/api/hr/payheads")
	public List<Payhead> getPayHeads(){
		List<Payhead>payHeads = new ArrayList<>();
		payHeadRepository.findAll().forEach(payHeads::add);
		return payHeads;
	}

	@PostMapping("/api/hr/tax")
	public ResponseEntity<?>addTax(@RequestBody Tax tax){
		tax.getReliefs().forEach(r->{
			System.out.println(r.getName());
		});
		tax.getNext().forEach(n->{
			System.out.println("N: "+n.getBand());
		});
		taxRepository.deleteAll();
		taxRepository.save(tax);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/api/hr/paytemplate/name/{name}")
	public String checkTemplateNmeExist(@PathVariable("name")String name){
		Optional<PayTemplate>template = payTemplateRepository.findByName(name);
		if(template.isEmpty()){
			return "OK";
		}
		return "EXIST";
	}

	@PostMapping("/api/hr/payroll/generate")
	public ResponseEntity<?>generatePayroll(@RequestBody Payroll payroll){
		logger.trace("GENERATE PAYSLIP");
		String[] dateStringContent = payroll.getDateString().split("-");
		LocalDate period =  LocalDate.of(Integer.parseInt(dateStringContent[1]),Integer.parseInt(dateStringContent[0]),1);
		payroll.setDate(period);
		General general = generalRepository.findAll().get(0);
		List<Payslip>payslips =new ArrayList<>();
		List<Payslip>payrollPayslips = payroll.getPayslips();
		LocalDate today = LocalDate.now();
		double totalGrossSalary = 0;
		double totalTax = 0;
		double totalNetSalary=0;
		double totalNssf = 0;
		double totalNhif = 0;
		payroll.setPayslips(payslips);
		double total = 0;
		for(Payslip payslip : payrollPayslips){
			payslip.setId(0);
			totalGrossSalary = totalGrossSalary + (payslip.getGrossSalary()+payslip.getNssf());
			totalTax =  totalTax + payslip.getNetTax();
			totalNetSalary = totalNetSalary +payslip.getNetSalary();
			totalNssf = totalNssf +payslip.getNssf();
			totalNhif = totalNhif + payslip.getNhif();
			payslip.setPayPeriod(payroll.getDate());
			List<Payhead>payheads =  new ArrayList<>();
			for(Payhead earning : payslip.getEarnings()){
				earning.setId(0);
				payheads.add(earning);
			}
			for(Payhead deduction : payslip.getDeductions()){
				deduction.setId(0);
				payheads.add(deduction);
			}
			for(Relief relief : payslip.getReliefs()){
				relief.setId(0);
			}
			payslip.setPayheads(payheads);
			total = total + payslip.getGrossSalary();
			payslips.add(payslip);
		}
		payroll.setGeneralId(general.getId());
		payroll.setNetSalaryTotal(totalNetSalary);
		payroll.setGrossSalaryTotal(totalGrossSalary);
		payroll.setNssfTotal(totalNssf);
		payroll.setNhifTotal(totalNhif);
		payrollRepository.save(payroll);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/api/hr/templates")
	public List<PayTemplate>getTemplatate(){
		List<Payslip>payslips = new ArrayList<>();
		List<PayTemplate>templates = new ArrayList<>();
		payTemplateRepository.findAll().forEach(templates::add);
		for(PayTemplate template : templates){
			if(template.getEmployees().size()>0){
				for(Employee employee : template.getEmployees()){
					employeeService.setStructure(employee);
				}
			}
			payrollService.createPayslipOnPayTemplate(template);
		}
		return templates;
	}

	@PostMapping("/api/hr/payroll/generate/{year}/{month}")
	public  ResponseEntity<?>generatePayroll(@PathVariable("year")String year,@PathVariable("month")String month,@RequestBody Payroll payroll){
		LocalDate date =  LocalDate.of(Integer.parseInt(year),Integer.parseInt(month),1);
		double totalDeductions = 0;
		payroll.setDate(date);
		for(Payslip ps : payroll.getPayslips()){
			ps.setPayPeriod(date);
			ps.setId(0);
			List<Payhead>payheads = new ArrayList<>();
			for(Payhead payhead : ps.getEarnings()){
				payhead.setType("earning");
				payhead.setId(0);
				payheads.add(payhead);
			}
			for(Payhead payhead : ps.getDeductions()){
				payhead.setType("deduction");
				totalDeductions= totalDeductions + payhead.getAmount();
				payhead.setId(0);
				payheads.add(payhead);
			}
			for(Relief relief: ps.getReliefs()) {
				relief.setId(0);
			}
			ps.setNetTax(Math.round(ps.getNetTax() * 100.0) / 100.0);
			ps.setNetSalary(ps.getGrossSalary()-(ps.getNetTax()+ ps.getTotalDeduction()));
			ps.setPayheads(payheads);
		}
		payrollRepository.save(payroll);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/api/hr/paytemplate")
	public ResponseEntity<PayTemplate>addPayTemplate(@RequestBody Payslip payslip){
		Optional<PayTemplate> existing = payTemplateRepository.findByName(payslip.getTemplateName());
		PayTemplate payTemplate = new PayTemplate();
		if(existing.isPresent()){
			payTemplate.setId(existing.get().getId());
		}
		List<Payhead>payheads = new ArrayList<>();
		double grossSalary= 0.00;
		for(Payhead ph:payslip.getEarnings()){
			ph.setType("earning");
			grossSalary =  grossSalary + ph.getAmount();
			payheads.add(ph);
		}
		for(Payhead ph:payslip.getDeductions()){
			ph.setType("deduction");
			payheads.add(ph);
		}
		List<Relief>reliefs = new ArrayList<>();
		List<Relief>everyOne =  reliefRepository.findByAllByEveryOne();
		payTemplate.setName(payslip.getTemplateName());
		payTemplate.setGrossSalary(grossSalary);
		payTemplate.setPayheads(payheads);
		payTemplate.setNssf(payslip.getNssf());
		payTemplate.setNhif(payslip.getNhif());
		payTemplateRepository.save(payTemplate);
		return new ResponseEntity<>(payTemplate,HttpStatus.OK);
	}

	@GetMapping("/api/hr/templates/search/sort/{text}")
	public ResponseEntity<List<PayTemplate>>getSearchedEmployees(@PathVariable("text")String text){
		List<PayTemplate>templates = new ArrayList<>();
		List<PayTemplate>searched = new ArrayList<>();
		payTemplateRepository.findAll().forEach(templates::add);
		for(PayTemplate template: templates){
			if(template.getName().toLowerCase().startsWith(text.toLowerCase())){
				searched.add(template);
			}
		}
		return new ResponseEntity<>(searched,HttpStatus.OK);
	}
	
	@PostMapping("/api/hr/template/preview")
	public ResponseEntity<Payslip>previewSlip(@RequestBody Payslip payslip){
		List<Tax>taxes = new ArrayList<>();
		taxRepository.findAll().forEach(taxes::add);
		Tax tax = taxes.get(0);
		Set<Relief>reliefs = tax.getReliefs().stream().filter(relief->relief.isEveryOne()).collect(Collectors.toSet());
		payslip.setReliefs(reliefs);
		double totalEarnings=0;
		double totalDeductions=0;
		if(payslip.getEarnings().size()>0){
			for (int i = 0; i < payslip.getEarnings().size(); i++) {
				totalEarnings = totalEarnings + payslip.getEarnings().get(i).getAmount();
			}
		}
		if(payslip.getDeductions().size()>0) {
			for(Payhead deduction : payslip.getDeductions()){
				totalDeductions = totalDeductions + deduction.getAmount();
			}
		}
		totalDeductions = totalDeductions+payslip.getNhif();
		double grossSalary = totalEarnings-payslip.getNssf();
		payslip.setGrossSalary(grossSalary);
		payslip.setTaxableIncome(grossSalary-payslip.getNssf());
		payslip = payrollService.calculateTax(payslip);
		double netSalary = grossSalary - (payslip.getNetTax() + totalDeductions);
		payslip.setTotalDeduction(totalDeductions);
		payslip.setNetSalary(netSalary);
		return new ResponseEntity<>(payslip,HttpStatus.OK);
	}

	@GetMapping("/api/hr/template/{text}")
	public ResponseEntity<PayTemplate>getPayslipByName(@PathVariable("text") String text){
		System.out.println("TEXT"+text);
		PayTemplate template= payTemplateRepository.findByName(text).get();
		payrollService.createPayslipOnPayTemplate(template);
		return new ResponseEntity<>(template,HttpStatus.OK);
	}

	@PostMapping("/api/hr/employee/template/add")
	public ResponseEntity<?>addEmployeeToTemplate(@RequestParam("employeeId")int employeeId,
												  @RequestParam("templateId")int templateId){
		PayTemplate template = payTemplateRepository.findById(templateId).get();
		Employee employee = employeeRepository.findById(employeeId).get();
		employee.setPayroll(true);
		employeeRepository.save(employee);
		Set<Employee>employees = template.getEmployees();
		employees.add(employee);
		payTemplateRepository.save(template);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/api/hr/payslip/calulate")
	public ResponseEntity<Payslip> calculatePayslip(@RequestBody Payslip payslip){
		double totalDeductions =0;
		double grossSalary =0;
		for(Payhead payhead: payslip.getEarnings()){
			grossSalary = grossSalary + payhead.getAmount();
		}
		for(Payhead payhead: payslip.getDeductions()){
			totalDeductions = totalDeductions + payhead.getAmount();
		}
		payslip.setGrossSalary(grossSalary- payslip.getNssf());
		Payslip slip = payrollService.calculateTax(payslip);
		slip.setId(payslip.getId());
		slip.setTotalDeduction(totalDeductions + payslip.getNhif());
		slip.setNetSalary(slip.getGrossSalary() - (slip.getNetTax() + slip.getTotalDeduction()));
		return new ResponseEntity<>(slip,HttpStatus.OK);
	}

	@PostMapping("/api/hr/payroll/calculate")
	public ResponseEntity<Payroll>calculatePayroll(@RequestParam("month")String month,
											       @RequestParam("year")String year){
		LocalDate period =LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
		Optional<Payroll>payrollExist = payrollRepository.findByPeriod(period);
		if(payrollExist.isPresent()){
			return new ResponseEntity<>(null,HttpStatus.ALREADY_REPORTED);
		}
		Payroll payroll = new Payroll();
		payroll.setDate(period);
		double total = 0.00;
		double tax=0.00;
		double nssf = 0.00;
		double nhif = 0.00;
		List<Employee>employees = new ArrayList<>();
		employees.addAll(employeeRepository.findByOnPayroll());
		List<Payslip>payslips = payrollService.getPayrollPayslips(employees,payroll.getDate());
		for(Payslip payslip : payslips){
			total = total + payslip.getGrossSalary() + payslip.getNssf();
			tax = tax + payslip.getNetTax();
			nssf =  nssf + payslip.getNssf();
			nhif = nhif + payslip.getNhif();
		}
		payroll.setPayslips(payslips);
		payroll.setTotalString(String.format("%.2f",total));
		payroll.setTax(SystemUtils.round(tax,2));
		payroll.setNssf(String.format("%.2f",nssf));
		payroll.setNhif(String.format("%.2f",nhif));
		return new ResponseEntity<>(payroll,HttpStatus.OK);
	}

	@GetMapping("/api/hr/payroll/remove/{id}")
	public ResponseEntity<?>removeEmployeeFormPayroll(@PathVariable("id") String id){
		int employeeId =  Integer.parseInt(id);
		Employee employee = employeeRepository.findById(employeeId).get();
		employee.setPayroll(false);
		employeeRepository.save(employee);
		Optional<Integer> templateId = payTemplateRepository.findByEmployeesId(employeeId);
		if(templateId.isPresent()){
			PayTemplate template = payTemplateRepository.findById(templateId.get()).get();
			Set<Employee>employees =  template.getEmployees();
			employees.removeIf(emp -> emp.getId() == employeeId);
			template.setEmployees(employees);
			payTemplateRepository.save(template);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/api/hr/template/remove/{id}")
	public ResponseEntity<?> deletePayTemplate(@PathVariable("id") String templateId){
		payTemplateRepository.deleteById(Integer.parseInt(templateId));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/api/hr/paytemplate/edit/{id}")
	public ResponseEntity<?> editPayTemplate(@PathVariable ("id")String id,@RequestBody Payslip template){
		PayTemplate payTemplate = payTemplateRepository.findById(Integer.parseInt(id)).get();
		List<Payhead>payheads = new ArrayList<>();
		for(Payhead payhead : template.getEarnings()){
			payhead.setType("earning");
			payheads.add(payhead);
		}
		for(Payhead payhead : template.getDeductions()){
			payhead.setType("deduction");
			payheads.add(payhead);
		}
		payTemplate.setPayheads(payheads);
		payTemplate.setNhif(template.getNhif());
		payTemplate.setNssf(template.getNssf());
		payTemplateRepository.save(payTemplate);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/api/test/tax/{gross}")
	public String testTtax(@PathVariable("gross")String gross){
		Double grossSalary =  Double.parseDouble(gross);
		return payrollService.testTax(grossSalary);
	}

	@GetMapping("/api/hr/general/code/{text}")
	public ResponseEntity<General> editEmployerCode(@PathVariable("text") String text){
		List<General>generals = new ArrayList<>();
		generalRepository.findAll().forEach(generals::add);
		General general = null;
		if(generals.size()<0){
			general = generals.get(0);
		}else{
			general = new General();
		}
		general.setEmployerCode(text);
		generalRepository.save(general);
		return new ResponseEntity<>(general,HttpStatus.OK);
	}

	@GetMapping("/api/hr/general/name/{text}")
	public ResponseEntity<General> editEmployerName(@PathVariable("text") String text){
		List<General>generals = generalRepository.findAll().stream().collect(Collectors.toList());
		General general = null;
		if(generals.size()>0){
			general= generals.get(0);
		}else{
			general = new General();
		}
		general.setEmployerName(text);
		generalRepository.save(general);
		return new ResponseEntity<>(general,HttpStatus.OK);
	}

	@PostMapping("/api/hr/users/edit")
	public ResponseEntity<?> editPassword(@RequestParam("newPassword")String newPassword,
										  @RequestParam("currentPassword")String oldPassword,
										  Authentication authentication){
		User user = userRepository.findByUsername(authentication.getName()).get();
		if(newPassword.length()<4){
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		if(!passwordEncoder.matches(oldPassword,user.getPassword())){
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/api/hr/users")
	public ResponseEntity<User>addUser(@RequestParam("username")String username,
									   @RequestParam("password")String password,
									   @RequestParam("employeeid")String employeeId,
									   @RequestParam("role")String role){
		if(username.length()<4){
			return new ResponseEntity<>(null,HttpStatus.CONFLICT);
		}
		if(password.length()<4){
			return new ResponseEntity<>(null,HttpStatus.CONFLICT);
		}
		User user = new User();
		user.setEmployeeId(Integer.parseInt(employeeId));
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		List<Role>roles =new ArrayList<>();
		if(role.equalsIgnoreCase("hr")){
			roles.add(new Role("HR"));
			roles.add(new Role("ADMIN"));
			user.setMainRole("HR");
		}
		else if(role.equalsIgnoreCase("admin")){
			roles.add(new Role("ADMIN"));
			user.setMainRole("ADMIN");
		}
		user.setRoles(roles);
		userRepository.save(user);
		return new ResponseEntity<>(user,HttpStatus.OK);
	}
	@GetMapping("/api/hr/users/delete/{id}")
	public ResponseEntity<?>deleteUser(@PathVariable("id")String id){
		List<Reminder>reminders = reminderRepository.findByUserId(Integer.parseInt(id));
		if(reminders.size()>0){
			reminders.forEach(reminder -> {reminderRepository.deleteById(reminder.getId());});
		}
		userRepository.deleteById(Integer.parseInt(id));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/api/hr/additional/payheads/{employeeId}")
	public ResponseEntity<ExtraPayhead>addAdditionalPayhead(@RequestParam("name")String name,
															@RequestParam("type")String type,
															@RequestParam("amount")String amount,
															@RequestParam("startMonth")String startMonth,
															@RequestParam("startYear")String startYear,
															@RequestParam("endMonth")String endMonth,
															@RequestParam("startYear")String endYear,
															@RequestParam("always")boolean always,
															@PathVariable("employeeId")String employeeId){
		ExtraPayhead extraPayhead = new ExtraPayhead();
		Employee employee = employeeRepository.findById(Integer.parseInt(employeeId)).get();
		if(!always){
			extraPayhead.setAlways(false);
			LocalDate startDate = LocalDate.of(Integer.parseInt(startYear),Integer.parseInt(startMonth),1);
			LocalDate endDate = LocalDate.of(Integer.parseInt(endYear),Integer.parseInt(endMonth),1);
			extraPayhead.setStartDate(startDate);
			extraPayhead.setEndDate(endDate);
		}
		else{
			extraPayhead.setAlways(true);
		}
		extraPayhead.setName(name);
		extraPayhead.setType(type);
		extraPayhead.setAmount(Double.parseDouble(amount));
		AdditionalPaymentDetails additionalPaymentDetails = null;
		Set<ExtraPayhead> extraPayheads =null;
		additionalPaymentDetails = employee.getAdditionalPaymentDetails();
		if( additionalPaymentDetails == null){
			additionalPaymentDetails = new AdditionalPaymentDetails();
		}
		extraPayheads = additionalPaymentDetails.getExtraPayheads();
		if(extraPayheads == null){
			extraPayheads = new HashSet<>();
		}
		extraPayheads.add(extraPayhead);
		additionalPaymentDetails.setExtraPayheads(extraPayheads);
		employee.setAdditionalPaymentDetails(additionalPaymentDetails);
		employeeRepository.save(employee);
		return new ResponseEntity<>(extraPayhead,HttpStatus.OK);
	}

	@GetMapping("/api/hr/extra/payhead/delete/{id}")
	public ResponseEntity<?>deleteExtraPayhead(@PathVariable ("id")String payheadId){
		extraPayheadRepository.deleteById(Integer.parseInt(payheadId));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/api/hr/additional/payheads/edit/{employeeId}/{payheadId}")
	public ResponseEntity<ExtraPayhead>editAdditionalPayhead(@RequestParam("name")String name,
															@RequestParam("type")String type,
															@RequestParam("amount")String amount,
															@RequestParam("startMonth")String startMonth,
															@RequestParam("startYear")String startYear,
															@RequestParam("endMonth")String endMonth,
															@RequestParam("startYear")String endYear,
															@RequestParam("always")boolean always,
															@PathVariable("employeeId")String employeeId,
															 @PathVariable("payheadId")String payheadId){
		Optional<ExtraPayhead> extraPayheadExist = extraPayheadRepository.findById(Integer.parseInt(payheadId));
		if(extraPayheadExist.isEmpty()){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		ExtraPayhead extraPayhead = extraPayheadExist.get();
		Employee employee = employeeRepository.findById(Integer.parseInt(employeeId)).get();
		if(!always){
			extraPayhead.setAlways(false);
			LocalDate startDate = LocalDate.of(Integer.parseInt(startYear),Integer.parseInt(startMonth),1);
			LocalDate endDate = LocalDate.of(Integer.parseInt(endYear),Integer.parseInt(endMonth),1);
			extraPayhead.setStartDate(startDate);
			extraPayhead.setEndDate(endDate);
		}
		else{
			extraPayhead.setAlways(true);
		}
		extraPayhead.setName(name);
		extraPayhead.setType(type);
		extraPayhead.setAmount(Double.parseDouble(amount));
		AdditionalPaymentDetails additionalPaymentDetails = null;
		Set<ExtraPayhead> extraPayheads =null;
		additionalPaymentDetails = employee.getAdditionalPaymentDetails();
		if( additionalPaymentDetails == null){
			additionalPaymentDetails = new AdditionalPaymentDetails();
		}
		extraPayheads = additionalPaymentDetails.getExtraPayheads();
		if(extraPayheads == null){
			extraPayheads = new HashSet<>();
		}
		extraPayheads.add(extraPayhead);
		additionalPaymentDetails.setExtraPayheads(extraPayheads);
		employee.setAdditionalPaymentDetails(additionalPaymentDetails);
		employeeRepository.save(employee);
		return new ResponseEntity<>(extraPayhead,HttpStatus.OK);
	}


}
