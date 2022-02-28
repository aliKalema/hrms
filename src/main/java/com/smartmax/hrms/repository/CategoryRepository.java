package com.smartmax.hrms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.smartmax.hrms.entity.Category;

public interface CategoryRepository extends JpaRepository<Category,Integer> {
	@Query(value="SELECT * FROM category WHERE name = ?",nativeQuery =true)
	Optional<Category> findByName(String location);
	@Query(value="SELECT category.name FROM category inner join employee ON category.id =  employee.category_id AND employee.id = ?",nativeQuery=true)
	Optional<String> findNameByEmployeeId(int id);
}
