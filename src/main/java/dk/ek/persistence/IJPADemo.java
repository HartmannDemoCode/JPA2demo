package dk.ek.persistence;

public interface IJPADemo {
    Employee updateDepartment(long empId, long deptId);
    Employee updateWithCheck(Employee incoming);
}
