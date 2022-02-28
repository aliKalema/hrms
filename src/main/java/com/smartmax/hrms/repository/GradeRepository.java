package com.smartmax.hrms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.smartmax.hrms.entity.Grade;

public interface GradeRepository extends CrudRepository<Grade,Integer>{
	Optional<Grade> findByName(String name);
    @Query(value="SELECT grade.name FROM grade inner join employee ON grade.id =  employee.grade_id AND employee.id = ?",nativeQuery=true)
    Optional<String> findNameByEmployeeId(int id);
}
