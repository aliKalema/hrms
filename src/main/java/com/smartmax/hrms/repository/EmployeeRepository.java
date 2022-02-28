package com.smartmax.hrms.repository;

import java.util.List;
import java.util.Optional;

import com.smartmax.hrms.entity.JobTitle;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.smartmax.hrms.entity.Employee;

public interface EmployeeRepository extends CrudRepository<Employee,Integer>{

	@Query(value="SELECT center_id FROM employee WHERE employee.id =?",nativeQuery=true)
	Optional<Integer>findCenterIdById(int id);
	
	@Query(value="SELECT job_title_id FROM employee WHERE employee.id =?",nativeQuery=true)
	Optional<Integer> findJobTitleIdById(int id);
	
	@Query(value="SELECT department_id FROM employee WHERE employee.id =?",nativeQuery=true)
	Optional<Integer> findDepartmentIdById(int id); 
	
	@Query(value="SELECT category_id FROM employee WHERE employee.id =?",nativeQuery=true)
	Optional<Integer> findCategoryIdById(int id);
	
	@Query(value="SELECT grade_id FROM employee WHERE employee.id =?",nativeQuery=true)
	Optional<Integer> findGradeIdById(int id);
	
	@Query(value="SELECT section_id FROM employee WHERE employee.id =?",nativeQuery=true)
	Optional<Integer> findSectionIdById(int id);
	
	@Query(value="SELECT employee_team_id FROM employee WHERE employee.id =?",nativeQuery=true)
	Optional<Integer> findTeamIdById(int id);
	
	@Query(value="SELECT * FROM employee WHERE employee.national_id =?",nativeQuery=true)
	Optional<Employee>findByNationalId(String nationalId);

	@Query(value="SELECT  FROM employee WHERE last_name =? AND first_name= ?",nativeQuery=true)
	Optional<Employee> findByName(String lastName, String firstName);

	@Query(value = "Select * FROM employee WHERE employee.payroll = 1",nativeQuery = true)
    List<Employee> findByOnPayroll();

	@Query(value="SELECT pay_template.net_salary FROM pay_template Inner JOIN pay_template_employees ON pay_template.id = pay_template_employees.pay_template_id AND pay_template_employees.employees_id = ?",nativeQuery = true)
	Optional<Double> findSalary(int id);

	@Query(value = "select job_title.name from employee inner join job_title ON  employee.job_title_id= job_title.id AND employee.id = ?",nativeQuery=true)
	Optional<String>findJobTitleByEmployeeId(int employeeId);

	@Query(value = "Select * FROM employee INNER JOIN leave_category ON employee.id = leave_category.employee_id INNER JOIN leavve ON leave.leave_category_id =  leave_category.id AND leave.id= ?",nativeQuery= true)
    Optional<Employee> findEmployeeByLeaveId(int id);

	@Query(value="SELECT employee.first_name, employee. .other_names FROM employee inner join team ON employee.employee_team_id =  team.id AND team.id = ?",nativeQuery=true)
	Optional<Employee>findManagerByEmployeeId(int teamId);


	@Query(value="SELECT employee.employee_team_id FROM employee WHERE employee.id = ?",nativeQuery= true)
	Optional<Integer> findTeamId(int id);

	@Query(value="SELECT * FROM employee WHERE employee.first_name= ? employee.other_names=?,employee.last_name=?",nativeQuery = true)
    Optional<Employee> findByNames(String firstName, String otherNames, String lastName);

	@Query(value="SELECT * FROM employee INNER JOIN leave_period ON  employee.id = leave_period.employee_id INNER JOIN leave_period_leaves ON leave_period.id = leave_period_leaves.leave_period_id AND leave_period_leaves.leaves_id = ?",nativeQuery=true)
    Optional<Employee> findByLeaveId(int id);

    List<Employee> findByActive(boolean b);
}
