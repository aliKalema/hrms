package com.smartmax.hrms.repository;

import com.smartmax.hrms.entity.LeaveCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface LeaveCategoryRepository extends CrudRepository<LeaveCategory,Integer> {
    Optional<LeaveCategory> findByName(String name);


//    @Query(value= "SELECT * FROM leave_category WHERE leave_category.main = 1 AND  leave_category.name = ?",nativeQuery=true)
//    Optional<LeaveCategory> findByNameAndMain(String name);
//    @Query(value="SELECT  * FROM leave_category INNER JOIN employee on leave_category.employee_id = employee.id AND employee.id = ? AND leave_category.name = ?",nativeQuery=true)
//    List<LeaveCategory> findByEmployeeIdAndLeaveCategoryName(int employeeId, String leaveCategoryName);
}
