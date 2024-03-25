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
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public class EmployeeDaoImpl implements EmployeeDao {
    private static final String GET_BY_ID = "SELECT * FROM employee WHERE id = ?";
    private static final String GET_ALL = "SELECT * FROM employee ";
    private static final String DELETE = "DELETE FROM employee WHERE id = ?";
    private static final String INSERT = "INSERT INTO employee " +
            "(id, firstname, lastname, middlename, " +
            "position, manager, hiredate, salary, department) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE employee SET firstname = ?, lastname = ?, " +
            " middlename = ?, position = ?, manager = ?, hiredate = ?, salary = ?, department = ? " +
            "WHERE id = ?";
    private static final String GET_BY_DEP = "SELECT * FROM employee WHERE department = ?";
    private static final String GET_BY_MANAGER = "SELECT * FROM employee WHERE manager = ?";
    List<Employee> employees;

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
            employees = new ArrayList<>();
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
            PreparedStatement statementInsert = connection.prepareStatement(INSERT);
            PreparedStatement statementUpdate = connection.prepareStatement(UPDATE);
            if (!employeeAlreadyExists(employee)) {
                insert(employee, statementInsert);
            } else {
                update(employee, statementUpdate);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return employee;
    }

    private boolean employeeAlreadyExists(Employee newEmployee) {
        for (Employee employee : employees) {
            if (employee.getId().equals(newEmployee.getId())) {
                return true;
            }
        }
        return false;
    }

    private static void insert(Employee employee, PreparedStatement statementInsert) throws SQLException {
        statementInsert.setBigDecimal(1, new BigDecimal(employee.getId()));
        statementInsert.setString(2, employee.getFullName().getFirstName());
        statementInsert.setString(3, employee.getFullName().getLastName());
        statementInsert.setString(4, employee.getFullName().getMiddleName());
        statementInsert.setString(5, employee.getPosition().name());
        statementInsert.setBigDecimal(6, new BigDecimal(employee.getManagerId()));
        statementInsert.setDate(7, Date.valueOf(employee.getHired()));
        statementInsert.setBigDecimal(8, employee.getSalary());
        statementInsert.setBigDecimal(9, new BigDecimal(employee.getDepartmentId()));
        statementInsert.execute();
    }

    private static void update(Employee employee, PreparedStatement statementUpdate) throws SQLException {
        statementUpdate.setString(1, employee.getFullName().getFirstName());
        statementUpdate.setString(2, employee.getFullName().getLastName());
        statementUpdate.setString(3, employee.getFullName().getMiddleName());
        statementUpdate.setString(4, employee.getPosition().name());
        statementUpdate.setBigDecimal(5, new BigDecimal(employee.getManagerId()));
        statementUpdate.setDate(6, Date.valueOf(employee.getHired()));
        statementUpdate.setBigDecimal(7, employee.getSalary());
        statementUpdate.setBigDecimal(8, new BigDecimal(employee.getDepartmentId()));
        statementUpdate.setBigDecimal(9, new BigDecimal(employee.getId()));
        statementUpdate.execute();
    }


    @Override
    public void delete(Employee employee) {
        try (Connection connection = ConnectionSource.instance().createConnection()) {
            PreparedStatement statement = connection.prepareStatement(DELETE);
            if (employeeAlreadyExists(employee)) {
                statement.setBigDecimal(1, new BigDecimal(employee.getId()));
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public List<Employee> getByDepartment(Department department) {
        List<Employee> employeesByDepartment = new ArrayList<>();
        try (Connection connection = ConnectionSource.instance().createConnection()){
            PreparedStatement statement = connection.prepareStatement(GET_BY_DEP);
            statement.setBigDecimal(1, new BigDecimal(department.getId()));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Employee employee = mapResultSetToEmployee(resultSet);
                employeesByDepartment.add(employee);
            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return employeesByDepartment;
    }

    @Override
    public List<Employee> getByManager(Employee employee) {
        List<Employee> employeesByManager = new ArrayList<>();
        try (Connection connection = ConnectionSource.instance().createConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_BY_MANAGER);
            statement.setBigDecimal(1, new BigDecimal(employee.getId()));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Employee employeeByManager = mapResultSetToEmployee(resultSet);
                employeesByManager.add(employeeByManager);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return employeesByManager;
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
