package com.smartmax.hrms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.smartmax.hrms.entity.Department;

public interface DepartmentRepository extends CrudRepository<Department,Integer> {
	Optional<Department>findByName(String name);

    @Query(value="SELECT department.name FROM department inner join employee ON department.id =  employee.department_id AND employee.id = ?",nativeQuery=true)
    Optional<String> findNameByEmployeeId(int id);
}
