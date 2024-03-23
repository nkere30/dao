package com.epam.rd.autocode.dao;

import com.epam.rd.autocode.ConnectionSource;
import com.epam.rd.autocode.domain.Department;
import com.epam.rd.autocode.domain.Employee;
import com.epam.rd.autocode.domain.FullName;
import com.epam.rd.autocode.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDaoImpl implements EmployeeDao {
    private static final String GET_BY_ID = "SELECT * FROM employee WHERE id = ?";
    private static final String GET_ALL = "SELECT * FROM employee ";
            ;
    private static final String SAVE = "INSERT INTO employee " +
            "(id, firstname, lastname, middlename, " +
            "position, manager, hireDate, salary, department) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    List<Employee> employees;

    public EmployeeDaoImpl() {
        employees = new ArrayList<>();
    }
    @Override

    public Optional<Employee> getById(BigInteger Id) {
        try (Connection connection = ConnectionSource.instance().createConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_BY_ID);
            statement.setBigDecimal(1, new BigDecimal(Id));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Employee employee = mapResultSetToEmployee(resultSet);
                return Optional.of(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return Optional.empty();
    }


    @Override
    public List<Employee> getAll() {
        try (Connection connection = ConnectionSource.instance().createConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_ALL);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Employee employee = mapResultSetToEmployee(resultSet);
                employees.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return employees;
    }

    @Override
    public Employee save(Employee employee) {
        try (Connection connection = ConnectionSource.instance().createConnection()) {
            PreparedStatement statement = connection.prepareStatement(SAVE);
            statement.setBigDecimal(1, new BigDecimal(employee.getId()));
            statement.setString(2, employee.getFullName().getFirstName());
            statement.setString(3, employee.getFullName().getLastName());
            statement.setString(4, employee.getFullName().getMiddleName());
            statement.setString(5, employee.getPosition().name());
            statement.setDate(6, java.sql.Date.valueOf(employee.getHired()));
            statement.setBigDecimal(7, employee.getSalary());
            statement.setBigDecimal(8, new BigDecimal(employee.getManagerId()));
            statement.setBigDecimal(9, new BigDecimal(employee.getDepartmentId()));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return null;
    }

    @Override
    public void delete(Employee employee) {

    }

    @Override
    public List<Employee> getByDepartment(Department department) {
        return null;
    }

    @Override
    public List<Employee> getByManager(Employee employee) {
        return null;
    }

    private Employee mapResultSetToEmployee(ResultSet resultSet) throws SQLException {
        BigInteger id = resultSet.getBigDecimal("id").toBigInteger();
        String firstName = resultSet.getString("firstName");
        String lastName = resultSet.getString("lastName");
        String middleName = resultSet.getString("middleName");
        FullName fullName = new FullName(firstName, lastName, middleName);
        Position position = Position.valueOf(resultSet.getString("position"));
        LocalDate hired = resultSet.getDate("hiredate").toLocalDate();
        BigDecimal salary = resultSet.getBigDecimal("salary");
        BigInteger managerId = resultSet.getBigDecimal("manager") != null ?
                resultSet.getBigDecimal("manager").toBigInteger() : new BigInteger(String.valueOf(0));
        BigInteger departmentId = resultSet.getBigDecimal("department") != null ?
                resultSet.getBigDecimal("department").toBigInteger() : new BigInteger(String.valueOf(0));
        return new Employee(id, fullName, position, hired, salary, managerId, departmentId);
    }
}
