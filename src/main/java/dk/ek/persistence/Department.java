package dk.ek.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    private String address;
    @OneToMany(mappedBy = "department", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    Set<Employee> employees = new HashSet<>();
    public void addEmployee(Employee employee){
        employees.add(employee);
        employee.setDepartment(this);
    }

    public void removeEmployee(Employee employee){
        employees.remove(employee);
        employee.setDepartment(null);
    }
}