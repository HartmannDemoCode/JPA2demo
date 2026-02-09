package dk.ek.persistence;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
            // Create 3 departments for testing
            for (int i = 1; i <= 3; i++) {
                Department department = new Department();
                department.setName("Department " + i);
                departmentDAO.create(department);
            }

            // Create 10 employees for testing
            for (int i = 1; i <= 10; i++) {
                Employee employee = new Employee();
                employee.setName("Employee " + i);
                employee.setEmail("employee" + i + "@example.com");
                employeeDAO.create(employee);
            }


    }

    @Test
    void create() {
    }

    @Test
    void get() {
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