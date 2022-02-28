package com.smartmax.hrms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.smartmax.hrms.entity.Section;

public interface SectionRepository extends CrudRepository<Section,Integer>{
	Optional<Section> findByName(String name);
    @Query(value="SELECT section.name FROM section inner join employee ON section.id =  employee.section_id AND employee.id = ?",nativeQuery=true)
    Optional<String> findNameByEmployeeId(int id);
}
