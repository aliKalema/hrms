package com.smartmax.hrms.repository;

import com.smartmax.hrms.entity.LeavePeriod;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LeavePeriodRepository extends CrudRepository<LeavePeriod,Integer> {
    @Query(value="SELECT * FROM leave_period WHERE leave_period.employee_id = ? AND leave_period.leave_category_id = ?",nativeQuery= true)
    List<LeavePeriod> findByEmployeeIdAndLeaveCategoryName(int employeeId, int categoryId);

    @Query(value = "SELECT * FROM leave_period INNER JOIN leave_period_leaves ON  leave_period_leaves.leave_period_id = leave_period.id INNER JOIN leavve ON  leave_period_leaves.leaves_id = leavve.id AND leavve.id  =?",nativeQuery=true)
    LeavePeriod findByLeaveId(int id);

    List<LeavePeriod> findByEmployeeId(int employeeId);
}
