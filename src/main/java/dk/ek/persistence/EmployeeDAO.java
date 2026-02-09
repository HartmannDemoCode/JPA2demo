package dk.ek.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.util.HashSet;
import java.util.Set;

public class EmployeeDAO implements IDAO<Employee> {
    EntityManagerFactory emf;
    public EmployeeDAO(EntityManagerFactory _emf){
        this.emf = _emf;
    }
    @Override
    public Employee create(Employee e) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.persist(e);
            em.getTransaction().commit();
            return e;
        }
    }

    @Override
    public Set<Employee> get() {
        try(EntityManager em = emf.createEntityManager()){
            return new HashSet(em.createQuery("SELECT e FROM Employee e").getResultList());
        }
    }

    @Override
    public Employee getByID(Long id) {
        try(EntityManager em = emf.createEntityManager()){
            Employee employee = em.find(Employee.class, id);
            if(employee == null)
                throw new EntityNotFoundException("No entity found with id: "+id);
            return employee;
        }
    }

    @Override
    public Employee update(Employee e) {
        try(EntityManager em = emf.createEntityManager()){
            Employee foundEmployee = em.find(Employee.class, e.getId());
            if(foundEmployee == null)
                throw new EntityNotFoundException("No entity found with id: "+e.getId());
            em.getTransaction().begin();
            Employee employee = em.merge(e);
            em.getTransaction().commit();
            return employee;
        }
    }

    @Override
    public Long delete(Employee e) {
        try(EntityManager em = emf.createEntityManager()){
            Employee employee = em.find(Employee.class, e.getId());
            if(employee == null)
                throw new EntityNotFoundException("No entity found with id: "+e.getId());
            em.getTransaction().begin();
            em.remove(employee);
            em.getTransaction().commit();
            return employee.getId();
        }
    }
}
