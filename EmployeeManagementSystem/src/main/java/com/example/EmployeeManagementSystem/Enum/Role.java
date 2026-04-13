package com.example.EmployeeManagementSystem.Enum;

import java.util.Collection;
import java.util.Set;

public enum Role {
    EMPLOYEE(Set.of(Permission.LEAVE_WRITE,Permission.LEAVE_CANCEL,Permission.SUBSCRIPTION_WRITE,Permission.SUBSCRIPTION_UPDATE,Permission.SUBSCRIPTION_READ)),
    MANAGER(Set.of(Permission.LEAVE_UPDATE,Permission.LEAVE_WRITE,Permission.SUBSCRIPTION_WRITE,Permission.SUBSCRIPTION_UPDATE,Permission.LEAVE_READ,Permission.SUBSCRIPTION_READ));
    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
     return permissions;
    }
}
