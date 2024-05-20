package com.creh.employees.service;

import com.creh.employees.model.Role;

import java.util.List;

public interface RoleService {
    List<Role> findAll();
    Role save(Role role);
}
