package com.smartmax.hrms.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.smartmax.hrms.entity.Relief;

import java.util.List;
import java.util.Optional;

public interface ReliefRepository extends CrudRepository<Relief,Integer>{
    @Query(value="SELECT * FROM relief WHERE name= ?",nativeQuery = true)
    Optional<Relief> findByName(String name);

    @Query(value="SELECT * FROM relief WHERE every_one =1",nativeQuery = true)
    List<Relief>findByAllByEveryOne();
}
