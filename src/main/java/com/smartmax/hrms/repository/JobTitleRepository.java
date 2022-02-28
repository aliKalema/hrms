package com.smartmax.hrms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.smartmax.hrms.entity.JobTitle;

public interface JobTitleRepository extends CrudRepository<JobTitle,Integer>{
	Optional<JobTitle> findByName(String jobTitleName);

	@Query(value="SELECT job_title.name FROM job_title inner join employee ON job_title.id =  employee.job_title_id AND employee.id = ?",nativeQuery=true)
	Optional<String>findByEmployeeId(int id);

	@Query(value="SELECT job_title.name FROM job_title inner join employee ON job_title.id =  employee.job_title_id AND employee.id = ?",nativeQuery=true)
    Optional<String> findNameByEmployeeId(int id);
}
