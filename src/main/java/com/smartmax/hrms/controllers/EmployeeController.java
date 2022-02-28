package com.smartmax.hrms.controllers;


import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.smartmax.hrms.entity.*;
import com.smartmax.hrms.repository.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.smartmax.hrms.service.EmployeeService;
import com.smartmax.hrms.service.FileService;
import com.smartmax.hrms.utils.SystemUtils;

@RestController
public class EmployeeController {
	@Autowired
	EmployeeRepository employeeRepository;
	
	@Autowired
	CenterRepository centerRepository;
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	SectionRepository sectionRepository;
	
	@Autowired
	DepartmentRepository departmentRepository;
	
	@Autowired
	JobTitleRepository jobTitleRepository;
	
	@Autowired
	GradeRepository gradeRepository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	TeamRepository teamRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	FileService fileService;
	
	@Autowired
	EmployeeService employeeService;

	@Autowired
	PayTemplateRepository payTemplateRepository;

	@Autowired
	SessionRegistry sessionRegistry;

	Logger logger =  LoggerFactory.getLogger(EmployeeController.class);

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handle(Exception e) {
		logger.warn("Returning HTTP 400 Bad Request", e);
	}

	@GetMapping("/api/admin/employees")
	public ResponseEntity<List<Employee>> getAllEmployees() {
		List<Employee>employees = new ArrayList<>();
		try {
			 employeeRepository.findAll().forEach(employee->{
				Employee employeeResponse = employeeService.generateEmployeeResponse(employee);
				employees.add(employeeResponse);
			});
		}
		catch(Exception e) {e.printStackTrace();}
		return new ResponseEntity<>(employees,HttpStatus.OK);
	}
	
	@GetMapping("/api/admin/employees/{id}")
	public ResponseEntity<Employee>getEmployeeById(@PathVariable("id") int id){
		Employee employee = null;
		Employee  employeeResponse= null;
		try {
			employee = employeeRepository.findById(id).get(); 
			employeeResponse = employeeService.generateEmployeeResponse(employee);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(employeeResponse,HttpStatus.OK);
	}
	
	@PutMapping("/api/admin/employees")
	public ResponseEntity<Employee>updateEmployee(@RequestBody Employee employee){
		employeeRepository.save(employee);
		return new ResponseEntity<>(employee,HttpStatus.OK);
    }
	
	@DeleteMapping("/api/admin/employees/delete")
	public ResponseEntity<?>deleteEmployee(@PathVariable("id")int id){
		employeeRepository.deleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/api/admin/employees/edit/{id}")
	public ResponseEntity<Employee>editEmployee(@PathVariable("id")String id,
												@RequestParam("nationalId")String nationalId,
												@RequestParam("firstName")String firstName,
												@RequestParam("lastName")String lastName,
												@RequestParam("otherNames")String otherNames,
												@RequestParam("department")String departmentName,
												@RequestParam(required= false,name="image")MultipartFile image,
												@RequestParam("phone")String phone,
												@RequestParam("email")String email,
												@RequestParam("payrollNumber")String payrollNumber,
												@RequestParam("paymentMode")String paymentMode,
												@RequestParam("accountPin")String accountPin,
												@RequestParam("jobTitle")String jobTitleName,
												@RequestParam("section")String sectionName,
												@RequestParam("category")String categoryName,
												@RequestParam("grade")String gradeName,
												@RequestParam("location")String location,
												@RequestParam("nhifPin")String nhifPin,
												@RequestParam("nssfPin")String nssfPin,
												@RequestParam("dob")String dob,
												@RequestParam("doj")String doj,
												@RequestParam("gender")String gender,
												@RequestParam("appo")String appoText,
												@RequestParam("kra")String kra,
												@RequestParam("bankName")String bankName,
												@RequestParam("branch")String branch,
												@RequestParam("imageSet")String imageSet,
												@RequestParam("team")String team,
												@RequestParam("employmentType")String employmentType)  {
		Optional<Employee> employeeOptional = employeeRepository.findById(Integer.parseInt(id));
		if (employeeOptional.isEmpty()) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		Employee employee = employeeOptional.get();
		boolean appo = appoText.equals("yes") ? true : false;
		employee.setAppo(appo);
		employee.setNationalId(nationalId);
		employee.setFirstName(firstName);
		employee.setLastName(lastName);
		employee.setOtherNames(otherNames);
		employee.setDob(SystemUtils.convertDateFormat(dob));
		employee.setDoj(SystemUtils.convertDateFormat(doj));
		employee.setEmail(email);
		employee.setPhone(phone);
		employee.setGender(gender);
		employee.setPaymentMode(paymentMode);
		employee.setAccountPin(accountPin);
		employee.setNssfPin(nssfPin);
		employee.setNhifPin(nhifPin);
		employee.setKra(kra);
		employee.setBankName(bankName);
		employee.setBranch(branch);
		employee.setEmploymentType(employmentType);
		employee.setPayrollNumber(payrollNumber);
		if (Integer.parseInt(imageSet) == 1) {
			if(employee.getImage()!=null) {
				try {
					File file = new ClassPathResource("src/main/resources/static/docs-upload/" + employee.getImage().getName()).getFile();
					file.delete();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			UserImage userImage = new UserImage();
			String imageName = "";
			try {
				imageName = fileService.addFile(image);
				userImage.setName(imageName);
				userImage.setMimeType(image.getContentType());
				userImage.setSize(image.getSize());
				employee.setImage(userImage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		employeeRepository.save(employee);
		Optional<Department> departmentOptional = departmentRepository.findByName(departmentName);
		if (departmentOptional.isPresent()) {
			Department department = departmentOptional.get();
			department.addEmployee(employee);
			departmentRepository.save(department);
		}
		Optional<Center> centerOptional = centerRepository.findByName(location);
		if (centerOptional.isPresent()) {
			Center center = centerOptional.get();
			center.addEmployee(employee);
			centerRepository.save(center);
		}
		Optional<Section> sectionOptional = sectionRepository.findByName(sectionName);
		if (sectionOptional.isPresent()) {
			Section section = sectionOptional.get();
			section.addEmployee(employee);
			sectionRepository.save(section);
		}
		Optional<JobTitle> jobTitleOptional = jobTitleRepository.findByName(jobTitleName);
		if (jobTitleOptional.isPresent()) {
			JobTitle jobTitle = jobTitleOptional.get();
			jobTitle.addEmployee(employee);
			jobTitleRepository.save(jobTitle);
		}
		Optional<Grade> gradeOptional = gradeRepository.findByName(gradeName);
		if (gradeOptional.isPresent()) {
			Grade grade = gradeOptional.get();
			grade.addEmployee(employee);
			gradeRepository.save(grade);
		}
		Optional<Category> categoryOptional = categoryRepository.findByName(categoryName);
		if (categoryOptional.isPresent()) {
			Category category = categoryOptional.get();
			category.addEmployee(employee);
			categoryRepository.save(category);
		}
		if (NumberUtils.isCreatable(team)) {
			int teamId = Integer.parseInt(team);
			Optional<Team> teamOptional = teamRepository.findById(teamId);
			if (teamOptional.isPresent()) {
				Team teamm = teamOptional.get();
				teamm.getEmployees().add(employee);
				teamRepository.save(teamm);
			}
		}
		return new ResponseEntity<>(employee, HttpStatus.OK);
	}

	@PostMapping("/api/admin/employees/add")
	public ResponseEntity<Employee>AddEmployee( @RequestParam("nationalId")String nationalId,
												@RequestParam("firstName")String firstName,
												@RequestParam("lastName")String lastName,
												@RequestParam("otherNames")String otherNames,
												@RequestParam("department")String departmentName,
												@RequestParam(required= false,name="image")MultipartFile image,
												@RequestParam("phone")String phone,
												@RequestParam("email")String email,
												@RequestParam("payrollNumber")String payrollNumber,
												@RequestParam("paymentMode")String paymentMode,
												@RequestParam("accountPin")String accountPin,
												@RequestParam("jobTitle")String jobTitleName,
												@RequestParam("section")String sectionName,
												@RequestParam("category")String categoryName,
												@RequestParam("grade")String gradeName,
												@RequestParam("location")String location,
												@RequestParam("nhifPin")String nhifPin,
												@RequestParam("nssfPin")String nssfPin,
												@RequestParam("dob")String dob,
												@RequestParam("doj")String doj,
												@RequestParam("gender")String gender,
												@RequestParam("appo")String appoText,
												@RequestParam("kra")String kra,
												@RequestParam("bankName")String bankName,
												@RequestParam("branch")String branch,
												@RequestParam("imageSet")String imageSet,
												@RequestParam("team")String team,
												@RequestParam("employmentType")String employmentType) {
		Employee employee = new Employee();
		boolean appo = appoText.equals("yes") ? true : false;
		employee.setAppo(appo);
		employee.setActive(true);
		employee.setNationalId(nationalId);
		employee.setFirstName(firstName);
		employee.setLastName(lastName);
		employee.setOtherNames(otherNames);
        Optional<Employee>employeeExist =employeeRepository.findByNationalId(nationalId);
		employee.setDob(SystemUtils.convertDateFormat(dob));
		employee.setDoj(SystemUtils.convertDateFormat(doj));
		employee.setEmail(email);
		employee.setPhone(phone);
		employee.setGender(gender);
		employee.setPaymentMode(paymentMode);
		employee.setAccountPin(accountPin);
		employee.setNssfPin(nssfPin);
		employee.setNhifPin(nhifPin);
		employee.setKra(kra);
		employee.setBankName(bankName);
		employee.setBranch(branch);
		employee.setEmploymentType(employmentType);
		employee.setPayrollNumber(payrollNumber);
		if (Integer.parseInt(imageSet) == 1) {
			UserImage userImage = new UserImage();
			String imageName = "";
			try {
				imageName = fileService.addFile(image);
				userImage.setName(imageName);
				userImage.setMimeType(image.getContentType());
				userImage.setSize(image.getSize());
				employee.setImage(userImage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		employeeRepository.save(employee);
		Optional<Department> departmentOptional = departmentRepository.findByName(departmentName);
		if (departmentOptional.isPresent()) {
			Department department = departmentOptional.get();
			department.addEmployee(employee);
			departmentRepository.save(department);
		}
		Optional<Center> centerOptional = centerRepository.findByName(location);
		if (centerOptional.isPresent()) {
			Center center = centerOptional.get();
			center.addEmployee(employee);
			centerRepository.save(center);
		}
		Optional<Section> sectionOptional = sectionRepository.findByName(sectionName);
		if (sectionOptional.isPresent()) {
			Section section = sectionOptional.get();
			section.addEmployee(employee);
			sectionRepository.save(section);
		}
		Optional<JobTitle> jobTitleOptional = jobTitleRepository.findByName(jobTitleName);
		if (jobTitleOptional.isPresent()) {
			JobTitle jobTitle = jobTitleOptional.get();
			jobTitle.addEmployee(employee);
			jobTitleRepository.save(jobTitle);
		}
		Optional<Grade> gradeOptional = gradeRepository.findByName(gradeName);
		if (gradeOptional.isPresent()) {
			Grade grade = gradeOptional.get();
			grade.addEmployee(employee);
			gradeRepository.save(grade);
		}
		Optional<Category> categoryOptional = categoryRepository.findByName(categoryName);
		if (categoryOptional.isPresent()) {
			Category category = categoryOptional.get();
			category.addEmployee(employee);
			categoryRepository.save(category);
		}
		if (NumberUtils.isCreatable(team)) {
			int teamId = Integer.parseInt(team);
			Optional<Team> teamOptional = teamRepository.findById(teamId);
			if (teamOptional.isPresent()) {
				Team teamm = teamOptional.get();
				teamm.getEmployees().add(employee);
				teamRepository.save(teamm);
			}
		}
		return new ResponseEntity<>(employee, HttpStatus.OK);
	}

	@PostMapping("/api/admin/employees/upload/csv")
	public ResponseEntity<?>uploadEmployeesCsv(@RequestParam("file") MultipartFile file){
		if(file.isEmpty()){
			return new ResponseEntity<>(null,HttpStatus.CONFLICT);
		}
		List<Employee>employees = employeeService.convertCsvToEmployees(file);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/api/admin/employees/search/sort/{text}")
	public ResponseEntity<List<Employee>>getSearchedEmployees(@PathVariable("text")String text){
		List<Employee>employees = new ArrayList<>();
		List<Employee>searched = new ArrayList<>();
		employeeRepository.findAll().forEach(employees::add);
		for(Employee employee: employees){
			if(employee.getFirstName().toLowerCase().contains(text.toLowerCase())
				||employee.getLastName().toLowerCase().contains(text.toLowerCase())
				||employee.getOtherNames().toLowerCase().contains(text.toLowerCase())
				||String.valueOf(employee.getNationalId()).contains(text)){
			if(employee.getImage()== null){
				UserImage userImage = new UserImage();
				userImage.setName("default-avatar.png");
				employee.setImage(userImage);
			}
			SystemUtils.getName(employee);
			searched.add(employee);
			}
		}
		return new ResponseEntity<>(searched,HttpStatus.OK);
	}

	@GetMapping("/api/hr/employees/reinstate/{id}")
	public ResponseEntity<?>reinstate(@PathVariable("id")String id){
		Optional<Employee> employee =  employeeRepository.findById(Integer.parseInt(id));
		if(employee.isEmpty()){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		employee.get().setActive(true);
		employeeRepository.save(employee.get());
		return new ResponseEntity(HttpStatus.OK);
	}

	@PostMapping("/api/hr/employee/terminate")
	public ResponseEntity<?>terminateEmployee(@RequestParam("eoc")String date,@RequestParam("id")String employeeId){
		Employee employee = employeeRepository.findById(Integer.parseInt(employeeId)).get();
		LocalDate eoc = SystemUtils.convertDateFormat(date,"dd-MM-yyyy");
		employee.setEoc(eoc);
		employee.setPayroll(false);
		employee.setActive(false);
		Optional<PayTemplate> payTemplate = payTemplateRepository.findByEmployeeId(Integer.parseInt(employeeId));
		if(payTemplate.isPresent()){
			List<Employee>employees = new ArrayList<>(payTemplate.get().getEmployees());
			for(int i =0;i<employees.size();i++){
				if(employees.get(i).getId() == employee.getId()){
					employees.remove(i);
				}
			}
			payTemplate.get().setEmployees(new HashSet<>(employees));
			payTemplateRepository.save(payTemplate.get());
		}
		employeeRepository.save(employee);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
