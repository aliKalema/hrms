package com.smartmax.hrms.repository;

import com.smartmax.hrms.entity.Leavve;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveRepository extends CrudRepository<Leavve,Integer> {
    @Query(value = "SELECT * FROM leavve WHERE ? between leavve.start_date AND leavve.end_date", nativeQuery=true)
    List<Leavve> findOnLeave(LocalDate today);

}
