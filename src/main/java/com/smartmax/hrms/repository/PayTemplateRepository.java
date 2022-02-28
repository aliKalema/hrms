package com.smartmax.hrms.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.smartmax.hrms.entity.PayTemplate;

import java.util.Optional;
import java.util.function.Supplier;

public interface PayTemplateRepository extends CrudRepository<PayTemplate,Integer> {

    Optional<PayTemplate>findByName(String name);

    @Query(value="SELECT * FROM Pay_template inner Join pay_template_employees ON pay_template_employees.pay_template_id = pay_template.id AND pay_template_employees.employees_id = ?",nativeQuery=true)
    Optional<PayTemplate>findByEmployeeId(int id);

    @Query(value="SELECT * FROM pay_template WHERE pay_template.id = ?",nativeQuery= true)
    Optional<PayTemplate>findById(int id);

    @Query(value="SELECT pay_template.id from pay_template inner join pay_template_employees on pay_template.id = pay_template_employees.pay_template_id AND pay_template_employees.employees_id = ?",nativeQuery= true)
    Optional<Integer>findByEmployeesId(int id);

}
