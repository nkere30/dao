package com.epam.rd.autocode.dao;

import com.epam.rd.autocode.ConnectionSource;
import com.epam.rd.autocode.domain.Department;
import com.epam.rd.autocode.domain.Employee;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepartmentDaoImpl implements DepartmentDao{
    private static final String GET_BY_ID = "SELECT * FROM department WHERE ID = ?";
    private static final String GET_ALL = "SELECT * FROM department";
    private static final String INSERT = "INSERT INTO department (id, name, location) VALUES (?, ?, ?)";
    private static final String UPDATE = "UPDATE department SET name = ?, location = ? WHERE ID = ?";
    private static final String DELETE = "DELETE * FROM department WHERE ID = ?";
    private List<Department> departments;


    public DepartmentDaoImpl() {
        departments = new ArrayList<>();
    }
    @Override
    public Optional<Department> getById(BigInteger Id) {
        try (Connection connection = ConnectionSource.instance().createConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_BY_ID);
            statement.setBigDecimal(1, new BigDecimal(Id));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Department department = mapResultSetToDepartments(resultSet);
                return Optional.of(department);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return Optional.empty();
    }

    @Override
    public List<Department> getAll() {
        try (Connection connection = ConnectionSource.instance().createConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_ALL);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Department department = mapResultSetToDepartments(resultSet);
                departments.add(department);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return departments;
    }

    @Override
    public Department save(Department department) {
        try (Connection connection = ConnectionSource.instance().createConnection()) {
            PreparedStatement statementCreate = connection.prepareStatement(INSERT);
            PreparedStatement statementUpdate = connection.prepareStatement(UPDATE);
            boolean exists = departmentAlreadyExists(department);
            if (exists) {
                statementUpdate.setString(1, department.getName());
                statementUpdate.setString(2, department.getLocation());
                statementUpdate.setString(3, String.valueOf(department.getId()));
                statementUpdate.execute();

            }
            else {
                statementCreate.setBigDecimal(1, new BigDecimal(department.getId()));
                statementCreate.setString(2, department.getName());
                statementCreate.setString(3, department.getLocation());
                statementCreate.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return department;
    }

    private boolean departmentAlreadyExists(Department department) {
        for (Department department1 : departments) {
            if (department1.getId().equals(department.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void delete(Department department) {
        try (Connection connection = ConnectionSource.instance().createConnection()){
            PreparedStatement statement = connection.prepareStatement(DELETE);
            statement.setBigDecimal(1, new BigDecimal(department.getId()));
            statement.execute();
        }catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Department mapResultSetToDepartments(ResultSet resultSet) throws SQLException {
        BigInteger id = resultSet.getBigDecimal("id").toBigInteger();
        String depName = resultSet.getString("name");
        String location = resultSet.getString("location");
        return new Department(id, depName, location);
    }
}
