package com.smartmax.hrms.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.smartmax.hrms.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartmax.hrms.repository.CategoryRepository;
import com.smartmax.hrms.repository.CenterRepository;
import com.smartmax.hrms.repository.DepartmentRepository;
import com.smartmax.hrms.repository.EmployeeRepository;
import com.smartmax.hrms.repository.GradeRepository;
import com.smartmax.hrms.repository.JobTitleRepository;
import com.smartmax.hrms.repository.SectionRepository;
import com.smartmax.hrms.repository.TeamRepository;
import com.smartmax.hrms.utils.SystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmployeeService {
	@Autowired
	EmployeeRepository employeeRepository;
	
	@Autowired
	CenterRepository centerRepository;
	
	@Autowired
	GradeRepository gradeRepository;
	
	@Autowired
	SectionRepository sectionRepository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	JobTitleRepository jobTitleRepository;
	
	@Autowired
	DepartmentRepository departmentRepository;
	
	@Autowired
	TeamRepository teamRepository;

	public void setStructure(Employee employee){
		Optional<String> department= departmentRepository.findNameByEmployeeId(employee.getId());
		department.ifPresent(employee::setDepartment);
		Optional<String> jobTitle= jobTitleRepository.findNameByEmployeeId(employee.getId());
		jobTitle.ifPresent(employee::setJobTitle);
		Optional<String> category= categoryRepository.findNameByEmployeeId(employee.getId());
		category.ifPresent(employee::setCategory);
		Optional<String> grade= gradeRepository.findNameByEmployeeId(employee.getId());
		grade.ifPresent(employee::setGrade);
		Optional<String> location = centerRepository.findNameByEmployeeId(employee.getId());
		location.ifPresent(employee::setLocation);
		Optional<String> section= sectionRepository.findNameByEmployeeId(employee.getId());
		section.ifPresent(employee::setSection);
		Optional<Team>team = teamRepository.findTeamByEmployeeId(employee.getId());
		if(team.isPresent()){
			Optional<Employee>manager = employeeRepository.findById(team.get().getManagerId());
			if(manager.isPresent()){
				SystemUtils.getName(manager.get());
				employee.setManagerName(manager.get().getName());
			}
		}
		SystemUtils.getName(employee);
		if(employee.getImage() ==null){
			UserImage image = new UserImage();
			image.setName("default-avatar.png");
			employee.setImage(image);
		}
	}
	
	public  Employee generateEmployeeResponse(Employee employee) {
		try{
			LocalDate today = LocalDate.now();
			Period periodAge = Period.between(employee.getDob(),today);
			Period periodExperience = Period.between(employee.getDoj(),today);
			employee.setAge(periodAge.getYears());
			employee.setExperience(periodExperience.getYears());
		}catch(Exception e){e.printStackTrace();}
		this.setStructure(employee);
		return employee;
	}

	public List<Employee> convertCsvToEmployees(MultipartFile file){
		List<Employee>employees = new ArrayList<>();
		String line ="";
		try {
			InputStream inputStream = file.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			while((line= bufferedReader.readLine())!=null){
				String[] row =line.split(",");
				System.out.println(row.length);
				if(row[0].equals("#")){
					System.out.println("HEADING");
				}
				else{
					Employee employee = new Employee();
					employee.setActive(true);
					if(row.length>1){
						if(row[1].length()>0){
							employee.setNationalId(row[1]);
						}
					}
					if(row.length>3){
						if(row[3].length()>0){
							employee.setName(row[3]);
						}
					}
					SystemUtils.setName(employee);
					if(row.length>4){
						if(row[4].length()>0) {
							employee.setDob(LocalDate.of(Integer.parseInt(row[4]), 1, 1));
						}
					}
					if(row.length>7){
						if(row[7].length()>0) {
							employee.setDoj(LocalDate.parse(row[7], DateTimeFormatter.ofPattern("dd/MM/yyyy")));
						}
					}
					if(row.length>8){
						if(row[8].length()>0) {
							employee.setDepartment(row[8]);
						}
					}
					if(row.length>9){
						if(row[9].length()>0) {
							employee.setSection(row[9]);
						}
					}
					if(row.length>10){
						if(row[10].length()>0) {
							employee.setCategory(row[10]);
						}
					}
					if(row.length>11){
						if(row[11].length()>0) {
							employee.setJobTitle(row[11]);
						}
					}
					if(row.length>12){
						if(row[12].length()>0) {
							employee.setLocation(row[12]);
						}
					}
					if(row.length>13){
						if(row[13].length()>0) {
							if (row[13].equalsIgnoreCase("perm")) {
								employee.setEmploymentType("PERMANENT");
							} else if (row[13].equalsIgnoreCase("cont")) {
								employee.setEmploymentType("CONTRACT");
							}
						}
					}
					if(row.length>14 && row[14].length()>0) {
						employee.setGrade(row[14]);
					}
					if(row.length>16){
						if(row[16].length()>0) {
							employee.setAppo(row[16].toLowerCase().equals("y"));
						}
					}
					if(row.length>18){
						if(row[18].length()>0) {
							employee.setNssfPin(row[18]);
						}
					}
					if(row.length>20){
						if(row[20].length()>0) {
							employee.setNhifPin(row[20]);
						}
					}
					if(row.length>22) {
						if(row[22].length()>0) {
							employee.setAccountPin(row[22]);
						}
					}
					if(row.length>24){
						if(row[24].length()>0) {
							employee.setKra(row[24]);
						}
					}
					if(row.length>25){
						if(row[25].length()>0) {
							employee.setPaymentMode(row[25]);
						}
					}
					System.out.println(employee.toString());
					System.out.println(employee.toString());
					Optional<Employee>employeeExist =  employeeRepository.findByNationalId(employee.getNationalId());

					if(employeeExist.isEmpty()){
						employeeRepository.save(employee);
						Optional<Department> departmentOptional = departmentRepository.findByName(employee.getDepartment());
						if (departmentOptional.isPresent()) {
							Department department = departmentOptional.get();
							department.addEmployee(employee);
							departmentRepository.save(department);
						}
						Optional<Center> centerOptional = centerRepository.findByName(employee.getLocation());
						if (centerOptional.isPresent()) {
							Center center = centerOptional.get();
							center.addEmployee(employee);
							centerRepository.save(center);
						}
						Optional<Section> sectionOptional = sectionRepository.findByName(employee.getSection());
						if (sectionOptional.isPresent()) {
							Section section = sectionOptional.get();
							section.addEmployee(employee);
							sectionRepository.save(section);
						}
						Optional<JobTitle> jobTitleOptional = jobTitleRepository.findByName(employee.getJobTitle());
						if (jobTitleOptional.isPresent()) {
							JobTitle jobTitle = jobTitleOptional.get();
							jobTitle.addEmployee(employee);
							jobTitleRepository.save(jobTitle);
						}
						Optional<Grade> gradeOptional = gradeRepository.findByName(employee.getGrade());
						if (gradeOptional.isPresent()) {
							Grade grade = gradeOptional.get();
							grade.addEmployee(employee);
							gradeRepository.save(grade);
						}
						Optional<Category> categoryOptional = categoryRepository.findByName(employee.getCategory());
						if (categoryOptional.isPresent()) {
							Category category = categoryOptional.get();
							category.addEmployee(employee);
							categoryRepository.save(category);
						}
						employees.add(employee);
					}
				}
			}
		}catch(IOException e){e.printStackTrace();}
		return employees;
	}
}
