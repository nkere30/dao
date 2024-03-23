package com.epam.rd.autocode.dao;

import com.epam.rd.autocode.domain.Department;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public class DepartmentDaoImpl implements DepartmentDao{
    @Override
    public Optional<Department> getById(BigInteger Id) {
        return Optional.empty();
    }

    @Override
    public List<Department> getAll() {
        return null;
    }

    @Override
    public Department save(Department department) {
        return null;
    }

    @Override
    public void delete(Department department) {

    }
}
