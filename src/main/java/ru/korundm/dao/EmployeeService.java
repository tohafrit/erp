package ru.korundm.dao;

import ru.korundm.entity.Employee;
import ru.korundm.entity.User;

import java.util.List;

public interface EmployeeService extends CommonService<Employee> {

    List<Employee> getAllActive(int status);

    List<Employee> getAllActiveByAreaId(int status, long areaId);

    Employee getActiveEmployeeByUser(User user, int status);
}