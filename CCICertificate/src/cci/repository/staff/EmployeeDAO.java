package cci.repository.staff;

import java.util.List;
import java.util.Map;

import cci.model.Department;
import cci.model.Employee;
import cci.repository.SQLBuilder;
import cci.web.controller.staff.ViewEmployee;

public interface EmployeeDAO {

	int getViewPageCount(SQLBuilder builder);
	List<ViewEmployee> findViewNextPage(int page, int pagesize, String orderby, String order, SQLBuilder builder);
	Map<String, Department> getDepartmentsList() throws Exception;
	Employee findEmployeeByID(Long id) throws Exception;
	Employee updateEmployee(Employee employee) throws Exception;
}
