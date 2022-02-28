package com.smartmax.hrms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.smartmax.hrms.entity.Role;


public interface RoleRepository extends JpaRepository<Role,Integer> {
   @Query(value="SELECT user_id FROM role  WHERE role_name = ?",nativeQuery =true)
   List<Integer>findAllUserIdByRoleName(String roleName);
}
