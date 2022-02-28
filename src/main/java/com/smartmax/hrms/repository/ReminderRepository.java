package com.smartmax.hrms.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.google.common.base.Optional;
import com.smartmax.hrms.entity.Reminder;

import java.util.List;

public interface ReminderRepository extends CrudRepository<Reminder,Integer> {
	@Query(value="SELECT * FROM reminder WHERE employee_id=?", nativeQuery=true)
	Optional<Reminder>findByEmployeeId(int id);
	
	@Query(value="SELECT * FROM reminder WHERE reminder.job_name=? ",nativeQuery=true)
	Optional<Reminder> findByJobName(String jobName);

	@Query(value="SELECT * FROM reminder WHERE setter_id = ?",nativeQuery = true)
	List<Reminder> findBySetterId(int setterId);

	@Query(value="SELECT * FROM reminder WHERE user_id = ? AND reminder.triggered = 0",nativeQuery = true)
    List<Reminder> findByUserIdAndNotTriggered(int id);

	@Query(value="SELECT * FROM reminder WHERE setter_id = ? AND reminder.unreceived = 1",nativeQuery = true)
	List<Reminder> findByUnReceived(int id);

	@Query(value="SELECT * FROM reminder WHERE user_id = ?",nativeQuery= true)
	List<Reminder> findByUserId(int id);
}
