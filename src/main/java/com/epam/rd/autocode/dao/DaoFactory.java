package com.epam.rd.autocode.dao;

import com.epam.rd.autocode.ConnectionSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DaoFactory {


    public EmployeeDao employeeDAO() {
        return new EmployeeDaoImpl();
    }

    public DepartmentDao departmentDAO() {
        return new DepartmentDaoImpl();
    }
}
