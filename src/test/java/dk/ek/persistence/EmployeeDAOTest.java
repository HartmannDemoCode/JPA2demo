package dk.ek.persistence;

import jakarta.persistence.EntityManagerFactory;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeDAOTest {
    private static EntityManagerFactory emf;
    private static IDAO<Employee> employeeDAO;
    private static DepartmentDAO departmentDAO;
    private final List<Employee> employees = new ArrayList<>();

    @BeforeAll
    static void setUp() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        employeeDAO = new EmployeeDAO(emf);
        departmentDAO = new DepartmentDAO(emf);
    }

    @BeforeEach
    void init() {
        // Clean up the database before each test
        employeeDAO.get().forEach(employee -> employeeDAO.delete(employee));
        departmentDAO.get().forEach(department -> departmentDAO.delete(department));
        // Create 3 departments for testing with 4 employees in each department
        employees.clear(); // reset the list of employees before each test
        for (int i = 1; i <= 3; i++) {
            Department department = new Department();
            department.setName("Department " + i);
            departmentDAO.create(department);
            for (int j = 1; j <= 4; j++) {
                Employee employee = new Employee();
                employee.setName("Employee " + j + " of Department " + i);
                employee.setEmail("employee" + j + "@department" + i + ".com");
                employee.setDepartment(department);
                employeeDAO.create(employee);
                employees.add(employee);
            }
        }
    }

    @AfterAll
    static void tearDown() {
        HibernateConfig.shutdownTestEmf();
    }

    @Test
    void create() {
    }

    @Test
    void get() {
        Set<Employee> eployees = employeeDAO.get();
        eployees.forEach(e -> System.out.println(e.getName() + " is deployed in department: " + e.getDepartment().getName()));
        int actual = eployees.size();
        int expected = 12;
        assertEquals(expected, actual);
    }

    @Test
    void getByID() {
    }

    @Test
    void update() {
        Employee employeeToUpdate = employees.get(0);
        employeeToUpdate.setName("Updated Employee Name");
        Employee updatedEmployee = employeeDAO.update(employeeToUpdate);
        assertEquals("Updated Employee Name", updatedEmployee.getName());
    }

    @Test
    void delete() {
    }

    @Test
    void generalUpdateWithCheck_updatesScalarsOnly() {
        // Arrange: create a detached "patch" employee (only ID + new name/email)
        Employee existing = employees.get(0);

        Employee patch = new Employee();
        patch.setId(existing.getId());
        patch.setName("Patched Name");
        patch.setEmail("patched@mail.com");

        // Act
        Employee updated = employeeDAO.updateWithCheck(patch);

        // Assert
        assertEquals("Patched Name", updated.getName());
        assertEquals("patched@mail.com", updated.getEmail());

        // department should remain unchanged
        assertNotNull(updated.getDepartment());
        assertEquals(existing.getDepartment().getId(), updated.getDepartment().getId());
    }

    @Test
    void generalUpdateWithCheck_movesEmployeeToExistingDepartment_withoutCascade() {
        // Arrange
        Employee existing = employees.get(0);
        Long empId = existing.getId();

        // pick a different department than the employee currently has
        Department currentDept = existing.getDepartment();
        Department targetDept = departmentDAO.get().stream()
                .filter(d -> !d.getId().equals(currentDept.getId()))
                .findFirst()
                .orElseThrow();

        Employee patch = new Employee();
        patch.setId(empId);

        Department deptRef = new Department();
        deptRef.setId(targetDept.getId());  // only ID is provided (detached reference)
        patch.setDepartment(deptRef);

        // Act
        Employee updated = employeeDAO.updateWithCheck(patch);

        // Assert
        assertNotNull(updated.getDepartment());
        assertEquals(targetDept.getId(), updated.getDepartment().getId());
    }


    @Test
    void generalUpdateWithCheck_persistsNewDepartment_whenNoId_withoutCascade() {
        // Arrange
        Employee existing = employees.get(0);

        Employee patch = new Employee();
        patch.setId(existing.getId());

        Department newDept = new Department();
        newDept.setName("Brand New Department"); // no id => should be persisted explicitly
        patch.setDepartment(newDept);

        int deptCountBefore = departmentDAO.get().size();

        // Act
        Employee updated = employeeDAO.updateWithCheck(patch);

        // Assert: employee now points to a department that has an id (was persisted)
        assertNotNull(updated.getDepartment());
        assertNotNull(updated.getDepartment().getId(), "New department should have been persisted explicitly");

        int deptCountAfter = departmentDAO.get().size();
        assertEquals(deptCountBefore + 1, deptCountAfter);

        // Verify name was stored
        Department fromDb = departmentDAO.getByID(updated.getDepartment().getId());
        assertEquals("Brand New Department", fromDb.getName());
    }
}