package com.creh.employees.controller;

import com.creh.employees.model.Role;
import com.creh.employees.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolesController {
    @Autowired
    private RoleService roleService;

    @GetMapping("/all")
    public List<Role> getAllEmployees() {
        return roleService.findAll();
    }
}
