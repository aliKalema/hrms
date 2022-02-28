package com.smartmax.hrms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.smartmax.hrms.entity.Center;

public interface CenterRepository extends CrudRepository<Center,Integer> {
	Optional<Center> findByName(String name);

    @Query(value="SELECT center.name FROM center inner join employee ON center.id =  employee.center_id AND employee.id = ?",nativeQuery=true)
    Optional<String> findNameByEmployeeId(int id);
}
