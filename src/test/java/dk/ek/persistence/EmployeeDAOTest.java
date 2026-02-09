package dk.ek.persistence;

import jakarta.persistence.EntityManagerFactory;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeDAOTest {
    private static EntityManagerFactory emf;
    private static EmployeeDAO employeeDAO;
    private static DepartmentDAO departmentDAO;

    @BeforeAll
    static void setUp() {
        HibernateConfig.setTestMode(true);
        emf = HibernateConfig.getEntityManagerFactory();
        employeeDAO = new EmployeeDAO(emf);
        departmentDAO = new DepartmentDAO(emf);
    }

    @BeforeEach
    void init() {
        // Clean up the database before each test
        employeeDAO.get().forEach(employee -> employeeDAO.delete(employee));
        departmentDAO.get().forEach(department -> departmentDAO.delete(department));
        // Create 3 departments for testing with 4 employees in each department
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
    }

    @Test
    void delete() {
    }
}