package com.smartmax.hrms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.smartmax.hrms.entity.Team;

public interface TeamRepository extends CrudRepository<Team,Integer> {
	@Query(value="SELECT  FROM team WHERE manager_id = ?", nativeQuery=true)
  Optional<Team>findByManagerId(int managerId);

	@Query(value="SELECT * FROM team INNER JOIN employee ON team.id = employee.employee_team_id AND employee.id = ?",nativeQuery=true)
    Optional<Team> findTeamByEmployeeId(int id);
}