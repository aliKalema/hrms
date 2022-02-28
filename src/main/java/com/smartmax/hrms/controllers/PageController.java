package com.smartmax.hrms.controllers;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import com.smartmax.hrms.entity.*;
import com.smartmax.hrms.repository.*;
import com.smartmax.hrms.service.LeaveService;
import com.smartmax.hrms.service.PayrollService;
import com.smartmax.hrms.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.smartmax.hrms.service.EmployeeService;
import com.smartmax.hrms.utils.SystemUtils;

@RestController
public class PageController {

	@Autowired
	SessionRegistry sessionRegistry;

	@Autowired
	DepartmentRepository departmentRepository;
	
	@Autowired
	SectionRepository sectionRepository;
	
	@Autowired
	CenterRepository centerRepository;
	
	@Autowired
	JobTitleRepository jobTitleRepository;
	
	@Autowired
	EmployeeRepository employeeRepository;
	
	@Autowired 
	GradeRepository gradeRepository;
	
	@Autowired 
	CategoryRepository categoryRepository;
	
	@Autowired
	TeamRepository teamRepository;
	
	@Autowired
	EmployeeService employeeService;
	
	@Autowired
	PayHeadRepository payHeadRepository;

	@Autowired
	TaxRepository taxRepository;

	@Autowired
	PayTemplateRepository payTemplateRepository;

	@Autowired
	ReminderRepository reminderRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PayrollRepository payrollRepository;

	@Autowired
	LeaveRepository leaveRepository;

	@Autowired
	LeaveCategoryRepository leaveCategoryRepository;

	@Autowired
	LeavePeriodRepository leavePeriodRepository;

	@Autowired
	LeaveService leaveService;

	@Autowired
	PayslipRepository payslipRepository;

	@Autowired
	ReminderService reminderService;

	@Autowired
	GeneralRepository generalRepository;

	@Autowired
	PayrollService payrollService;

	@GetMapping("/admin/employee")
	public ModelAndView getEmployeePage(Authentication authentication) {
		ModelAndView mav = new ModelAndView();
		List<Employee>employees = new ArrayList<>();
		employeeRepository.findByActive(true).forEach(employee->{
			Employee empl = employeeService.generateEmployeeResponse(employee);
			employees.add(empl);
		});
		List<Employee>terminated = new ArrayList<>();
		employeeRepository.findByActive(false).forEach(employee->{
			Employee empl = employeeService.generateEmployeeResponse(employee);
			terminated.add(empl);
		});
		String username = authentication.getName();
		User user =  userRepository.findByUsername(username).get();
		if(user.getEmployee() == null){
			user.setEmployee(new Employee(new UserImage("default-avatar.png")));
		}
		List<Reminder>reminders = new ArrayList<>();
		List<Reminder>unreceivedReminders =  new ArrayList<>();
		List<Reminder>allReminders = reminderRepository.findByUserId(user.getId());
		for(Reminder reminder : allReminders){
			if(reminder.isTriggered()){
				if(reminder.isReceived()){
					reminderRepository.deleteById(reminder.getId());
				}
				else{
					reminder.setDate(reminder.getDateTime().toLocalDate());
					reminder.setTime(reminder.getDateTime().toLocalTime());
					String day  = reminder.getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
					reminder.setDay(day);
					unreceivedReminders.add(reminder);
				}
			}
			else{
				reminder.setDate(reminder.getDateTime().toLocalDate());
				reminder.setTime(reminder.getDateTime().toLocalTime());
				String day  = reminder.getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
				reminder.setDay(day);
				reminders.add(reminder);
			}
		}
		user.removePassword();
		mav.addObject("user",user);
		mav.addObject("terminated",terminated);
		mav.addObject("employees",employees);
		mav.addObject("reminders",reminders);
		mav.addObject("unreceivedReminders",unreceivedReminders);
		mav.setViewName("employees");
		return mav;
	}

	@GetMapping("/admin/employee/edit/{id}")
	public ModelAndView editEmployee(@PathVariable("id")String id,Authentication authentication){
		ModelAndView mav = new ModelAndView();
		Optional<Employee>employee = employeeRepository.findById(Integer.parseInt(id));
		if(employee.isEmpty()){
			return new ModelAndView("error/404");
		}
		if(employee.get().getImage() == null){
			UserImage image = new UserImage();
			image.setName("default-avatar.png");
			employee.get().setImage(image);
		}
		employeeService.setStructure(employee.get());
		List<JobTitle>jobTitles = new ArrayList<>();
		jobTitleRepository.findAll().forEach(jobTitles::add);
		List<Department>departments = new ArrayList<>();
		departmentRepository.findAll().forEach(departments::add);
		List<Section>sections = new ArrayList<>();
		sectionRepository.findAll().forEach(sections::add);
		List<Center>centers = new ArrayList<>();
		centerRepository.findAll().forEach(centers::add);
		List<Grade>grades = new ArrayList<>();
		gradeRepository.findAll().forEach(grades::add);
		List<Category>categories = new ArrayList<>();
		categoryRepository.findAll().forEach(categories::add);
		List<Team>teams = new ArrayList<>();
		teamRepository.findAll().forEach(teams::add);
		teams.forEach(team->{
			if(team.getManagerId()>0) {
				Employee manager = employeeRepository.findById(team.getManagerId()).get();
			}
		});
		reminderService.setUnReceivedReminders(mav,authentication);
		mav.addObject("locations",centers);
		mav.addObject("departments",departments);
		mav.addObject("sections",sections);
		mav.addObject("categories",categories);
		mav.addObject("grades",grades);
		mav.addObject("jobtitles",jobTitles);
		mav.setViewName("edit_employee");
		mav.addObject("employee",employee.get());
		return mav;
	}
	
	@GetMapping("/admin/structure")
	public ModelAndView getStructurePage(Authentication authentication) {
		ModelAndView mav = new ModelAndView();
		List<Department>departments =  new ArrayList<>();
		departmentRepository.findAll().forEach(departments::add);
		for(Department department: departments){
			department.setHodImage("default-avatar.png");
			if(department.getHodId()!=0){
				Optional<Employee> employee =  employeeRepository.findById(department.getHodId());
				if(employee.isPresent()){
					Employee empl = employee.get();
					employeeService.setStructure(empl);
					department.setHodName(empl.getName());
					department.setHodImage(empl.getImage().getName());
				}
			}
			department.getEmployees().forEach(employee -> {
				employeeService.setStructure(employee);
			});
		}
		List<JobTitle>jobTitles =  new ArrayList<>();
		jobTitleRepository.findAll().forEach(jobTitles::add);
		for(JobTitle jobTitle: jobTitles){
			jobTitle.getEmployees().forEach(employee -> {
				employeeService.setStructure(employee);
			});
		}
		List<Center>locations =  new ArrayList<>();
		centerRepository.findAll().forEach(locations::add);
		for(Center center: locations){
			center.getEmployees().forEach(employee -> {
				employeeService.setStructure(employee);
			});
		}
		List<Section>sections =  new ArrayList<>();
		sectionRepository.findAll().forEach(sections::add);
		for(Section section: sections){
			section.getEmployees().forEach(employee -> {
				employeeService.setStructure(employee);
			});
		}
		List<Category>categories =  new ArrayList<>();
		categoryRepository.findAll().forEach(categories::add);
		for(Category category: categories){
			category.getEmployees().forEach(employee -> {
				employeeService.setStructure(employee);
			});
		}
		List<Team>teams = new ArrayList<>();
		teamRepository.findAll().forEach(teams:: add);
		for(Team team:teams){
			Optional<Employee>employee = employeeRepository.findById(team.getManagerId());
			if(employee.isPresent()){
				employeeService.setStructure(employee.get());
				team.setManager(employee.get());
			}
			team.getEmployees().forEach(e->{employeeService.setStructure(e);});
		}
		reminderService.setUnReceivedReminders(mav,authentication);
		mav.addObject("departments",departments);
		mav.addObject("teams",teams);
		mav.addObject("locations",locations);
		mav.addObject("jobTitles",jobTitles);
		mav.addObject("sections",sections);
		mav.addObject("categories",categories);
		mav.setViewName("structure");
		return mav;
	}

	@GetMapping("/hr/general")
	public ModelAndView getSettingPage(Authentication authentication){
		ModelAndView mav = new ModelAndView();
		List<UserDetails> usersDetails= sessionRegistry.getAllPrincipals()
				.stream()
				.filter(principal -> principal instanceof UserDetails)
				.map(UserDetails.class::cast)
				.collect(Collectors.toList());
		List<User>users = userRepository.findAll().stream().collect(Collectors.toList());
		users.forEach(user->{
			if(user.getRoles().size()==1){
				user.setMainRole("ADMIN");
			}
			else if(user.getRoles().size()==2){
				user.setMainRole("HR");
			}
			for(int  i =0;i<usersDetails.size();i++) {
				if(usersDetails.get(i).getUsername().equals(user.getUsername())) {
					user.setOnline(true);
				}
			}
		});
		List<General>generals = new ArrayList<>();
		generalRepository.findAll().forEach(generals::add);
		General general = null;
		if(generals.size()>0){
			general = generals.get(0);
		}
		if(general == null){
			general = new General("NULL","NULL");
		}
		reminderService.setUnReceivedReminders(mav,authentication);
		mav.addObject("users",users);
		mav.addObject("general",general);
		mav.setViewName("general");
		return mav;
	}
	
	@GetMapping("/hr/template")
	public ModelAndView getPayHead(Authentication authentication) {
		ModelAndView mav = new ModelAndView("paytemplate");
		reminderService.setUnReceivedReminders(mav,authentication);
		return mav;
	}

	@GetMapping("/admin/employee/add")
	public ModelAndView addEmployeePage(Authentication authentication){
		ModelAndView mav = new ModelAndView();
		List<JobTitle>jobTitles = new ArrayList<>();
		jobTitleRepository.findAll().forEach(jobTitles::add);
		List<Department>departments = new ArrayList<>();
		departmentRepository.findAll().forEach(departments::add);
		List<Section>sections = new ArrayList<>();
		sectionRepository.findAll().forEach(sections::add);
		List<Center>centers = new ArrayList<>();
		centerRepository.findAll().forEach(centers::add);
		List<Grade>grades = new ArrayList<>();
		gradeRepository.findAll().forEach(grades::add);
		List<Category>categories = new ArrayList<>();
		categoryRepository.findAll().forEach(categories::add);
		List<Team>teams = new ArrayList<>();
		teamRepository.findAll().forEach(teams::add);
		teams.forEach(team->{
			if(team.getManagerId()>0) {
				Employee manager = employeeRepository.findById(team.getManagerId()).get();
			}
		});
		reminderService.setUnReceivedReminders(mav,authentication);
		mav.addObject("locations",centers);
		mav.addObject("departments",departments);
		mav.addObject("sections",sections);
		mav.addObject("categories",categories);
		mav.addObject("grades",grades);
		mav.addObject("jobtitles",jobTitles);
		mav.setViewName("add_employee");
		return mav;
	}
	
	@GetMapping("/admin/employee/{id}")
	public ModelAndView getSingleEmployeePage(@PathVariable("id") int id,Authentication authentication) {
		ModelAndView mav = new ModelAndView();
		Optional<Employee> employee =employeeRepository.findById(id);
		if(employee.isEmpty()){
			return new ModelAndView("error/404");
		}
		employeeService.setStructure(employee.get());
		Optional<PayTemplate>payTemplate = payTemplateRepository.findByEmployeeId(employee.get().getId());
		if(payTemplate.isPresent()){
			payrollService.createPayslipOnPayTemplate(payTemplate.get());
			mav.addObject("payTemplate",payTemplate.get());
		}
		List<Leavve>leaves = new ArrayList<>();
		int year = LocalDate.now().getYear();
		List<LeavePeriod>leavePeriods = leavePeriodRepository.findByEmployeeId(employee.get().getId());
		List<Leavve>leave = new ArrayList<>();
		LocalDate today = LocalDate.now();
		for(LeavePeriod leavePeriod : leavePeriods){
				for(Leavve lv :leavePeriod.getLeaves()){
					lv.setCategoryName(leavePeriod.getLeaveCategory().getName());
					lv.setCategoryRotate(leavePeriod.getLeaveCategory().getRotate());
					lv.setDaysOnLeave();
					leaves.add(lv);
				}
		}
		if(leaves.size()>0){
			Collections.sort(leaves, (o1, o2) -> o2.getStartDate().compareTo(o1.getStartDate()));
		}
		reminderService.setUnReceivedReminders(mav,authentication);
		List<ViableLeaveCategory> leaveCategories = leaveService.getEmployeeViableLeaves(id);
		mav.addObject("employee",employee.get());
		mav.addObject("leaveCategories",leaveCategories);
		mav.addObject("leaves",leaves);
		mav.setViewName("employee");
		return mav;
	}
	
	@GetMapping("/admin/dashboard")
	public ModelAndView getDashboardPage(Authentication authentication) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("dashboard");
		String username = authentication.getName();
		User user =  userRepository.findByUsername(username).get();
		if(user.getEmployee() == null){
			user.setEmployee(new Employee(new UserImage("default-avatar.png")));
		}
		mav.addObject("user",user);
		return mav;
	}
	
	@GetMapping("/hr/template/add")
	public ModelAndView getPayTemplate(Authentication authentication) {
		ModelAndView mav = new ModelAndView();
		reminderService.setUnReceivedReminders(mav,authentication);
		mav.setViewName("add_paytemplate");
		return mav;
	}
	
	@GetMapping("/login")
	public ModelAndView getLoginPage() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("login");
		return mav;
	}
	
	@GetMapping("/hr/payroll")
	public ModelAndView getPayRoll(Authentication authentication) {
		ModelAndView mav = new ModelAndView();
		List<Employee>employees =  employeeRepository.findByOnPayroll();
		for(Employee employee: employees){
			Optional<PayTemplate> template = payTemplateRepository.findByEmployeeId(employee.getId());
			employeeService.setStructure(employee);
			SystemUtils.getName(employee);
			if(template.isPresent()){
				employee.setSalary(template.get().getGrossSalary()+ template.get().getNssf());
			}
		}
		reminderService.setUnReceivedReminders(mav,authentication);
		mav.addObject("employees",employees);
		mav.setViewName("payroll");
		return mav;
	}
	
	@GetMapping("/hr/payroll/add")
	public ModelAndView addToPayRoll(Authentication authentication) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("addToPayRoll");
		reminderService.setUnReceivedReminders(mav,authentication);
		return mav;
	}

	@GetMapping("/admin/leave")
	public ModelAndView getLeavePage(Authentication authentication){
		ModelAndView mav = new ModelAndView();
		LocalDate today = LocalDate.now();
		mav.setViewName("leave");
		List<Leavve>leaves = leaveService.getupCommingLeaves();
		List<Leavve>onLeave = leaveService.getOnLeave();
		List<LeaveCategory>leaveCategories = new ArrayList<>();
		leaveCategoryRepository.findAll().forEach(leaveCategories::add);
		reminderService.setUnReceivedReminders(mav,authentication);
		mav.addObject("categories",leaveCategories);
		mav.addObject("leaves",leaves);
		mav.addObject("onLeave",onLeave);
		return mav;
	}

	@GetMapping("/hr/template/edit/{id}")
	public ModelAndView editPayTemplate(@PathVariable("id")String id,Authentication authentication){
		Optional <PayTemplate> template = payTemplateRepository.findById(Integer.parseInt(id));
		List<Payhead>earnings = new ArrayList<>();
		List<Payhead>deductions = new ArrayList<>();
		if(template.isEmpty()){
			return new ModelAndView("/error/404");
		}
		for(Payhead payhead: template.get().getPayheads()){
			if(payhead.getType().equalsIgnoreCase("earnings")){
				earnings.add(payhead);
			}
			else{
				deductions.add(payhead);
			}
		}
		template.get().setDeduction(deductions);
		template.get().setEarnings(earnings);
		ModelAndView mav = new ModelAndView();
		reminderService.setUnReceivedReminders(mav,authentication);
		mav.addObject("template",template.get());
		mav.setViewName("edit_paytemplate");
		return mav;
	}

	@GetMapping("/hr/users")
	public ModelAndView getUsersPage(Authentication authentication) {
		ModelAndView mav = new ModelAndView();
		List<User> users = userRepository.findAll();
		List<UserDetails> usersDetails = sessionRegistry.getAllPrincipals()
				.stream()
				.filter(principal -> principal instanceof UserDetails)
				.map(UserDetails.class::cast)
				.collect(Collectors.toList());
		for (User user: users){
			for (UserDetails usersDetail : usersDetails) {
				if (usersDetail.getUsername().equals(user.getUsername())) {
					user.setOnline(true);
				}
			}
		}
		reminderService.setUnReceivedReminders(mav,authentication);
		mav.addObject("users",users);
		mav.setViewName("users");
		return mav;
	}

	@GetMapping("/admin/leave/category")
	public ModelAndView leaveCategoryPage(Authentication authentication){
		ModelAndView mav =  new ModelAndView();
		List<LeaveCategory>leaveCategories = new ArrayList<>();
		leaveCategoryRepository.findAll().forEach(leaveCategories::add);
		reminderService.setUnReceivedReminders(mav,authentication);
		mav.addObject("categories",leaveCategories);
		mav.setViewName("leave_category");
		return mav;
	}

	@GetMapping("/hr/payroll/{year}/{month}")
	public ModelAndView getpayrollMonth(@PathVariable("year")String year,@PathVariable("month")String month,Authentication authentication){
		LocalDate date =  LocalDate.of(Integer.parseInt(year),Integer.parseInt(month),1);
		Optional<Payroll> payroll = payrollRepository.findByDate(date);
		double totalGrossSalary= 0;
		double nssf = 0;
		double nhif = 0;
		if(payroll.isEmpty()){
			return new ModelAndView("error/404");
		}

		for(Payslip payslip: payroll.get().getPayslips()){
			SystemUtils.getName(payslip.getEmployee());
		}
		String  dateString =  payroll.get().getDate().getMonth() + ", "+ String.valueOf(payroll.get().getDate().getYear());
		payroll.get().setDateString(dateString);
		for(Payslip payslip :payroll.get().getPayslips()){
			employeeService.setStructure(payslip.getEmployee());
			List<Payhead>earnings = new ArrayList<>();
			List<Payhead>deductions = new ArrayList<>();
			double totalDeductions = 0;
			for(Payhead payhead :payslip.getPayheads()){
				if(payhead.getType().equalsIgnoreCase("earning")){
					earnings.add(payhead);
				}
				else if(payhead.getType().equalsIgnoreCase("deduction")){
					deductions.add(payhead);
					totalDeductions= totalDeductions + payhead.getAmount();
				}
			}
			totalDeductions = totalDeductions + payslip.getNhif();
			payslip.setEarnings(earnings);
			payslip.setDeductions(deductions);
			payslip.setTotalDeduction(totalDeductions);
			totalGrossSalary= totalGrossSalary + payslip.getGrossSalary() + payslip.getNssf();
			nssf = nssf + payslip.getNssf();
			nhif = nhif +payslip.getNhif();
		}
		payroll.get().setNhifTotal((double)Math.round(nhif * 100) / 100);
		payroll.get().setNssfTotal((double)Math.round(nssf * 100) / 100);
		payroll.get().setTotal((double) Math.round(totalGrossSalary * 100) / 100);
		ModelAndView mav =new ModelAndView();
		reminderService.setUnReceivedReminders(mav,authentication);
		mav.addObject("payroll",payroll.get());
		mav.setViewName("payroll_month");
		return mav;
	}

}