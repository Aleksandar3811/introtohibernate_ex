import entities.*;

import javax.persistence.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public class main {
    private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("softuni_jpa");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        ex13(entityManager);
        entityManager.getTransaction().commit();
    }

    private static void ex13(EntityManager entityManager) throws IOException {
        List<Town> result = entityManager.createQuery("FROM Town WHERE name=:name", Town.class)
                .setParameter("name", READER.readLine())
                .getResultList();

        if (!result.isEmpty()) {
            Town town = result.get(0);
            List<Address> addreses = entityManager.createQuery("SELECT a FROM Address a JOIN a.town t  WHERE  t.name=:name", Address.class)
                    .setParameter("name", town.getName())
                    .getResultList();

            addreses.forEach(a->{
                a.getEmployees().forEach(e->{
                    e.setAddress(null);
                    entityManager.persist(e);
                });
                entityManager.remove(a);

                    });
            System.out.printf("%s address in %s was deleted", addreses.size(), town.getName());
            entityManager.remove(town);
        }
    }

    private static void ex12(EntityManager entityManager) {
        List<Department> departments = entityManager.createQuery("FROM Department", Department.class).getResultList();
        departments.forEach(d -> {
            double departmentSalary = d.getEmployees()
                    .stream()
                    .mapToDouble(e -> e.getSalary().doubleValue())
                    .max().orElse(0);
            if (departmentSalary < 30000 || departmentSalary > 70000) {
                System.out.printf("%s %.2f%n", d.getName(), departmentSalary);
            }
        });
    }

    private static void ex11(EntityManager entityManager) throws IOException {
        String pattern = READER.readLine();
        entityManager.createQuery("FROM Employee WHERE firstName LIKE CONCAT(:pattern,'%')", Employee.class)
                .setParameter("pattern", pattern).getResultStream()
                .forEach(e -> System.out.printf("%s %s - %s - %.2f%N", e.getFirstName(), e.getLastName(), e.getJobTitle(), e.getSalary()));
    }

    private static void ex10(EntityManager entityManager) {
        List<Employee> employees = entityManager.createQuery
                        ("SELECT e FROM Employee e  JOIN e.department d WHERE d.name IN('Engineering', 'Tool Design', 'Marketing', 'Information Services')", Employee.class)
                .getResultList();
        for (Employee employee : employees) {
            employee.setSalary(employee.getSalary().multiply(BigDecimal.valueOf(1.12)));
            entityManager.persist(employee);
            System.out.printf("%s %s (%.2f)%n", employee.getFirstName(), employee.getLastName(), employee.getSalary());

        }


    }

    private static void ex9(EntityManager entityManager) {
        entityManager.createQuery("FROM Project ORDER BY startDate DESC, name", Project.class)
                .setMaxResults(10).getResultStream()
                .forEach(p -> System.out.printf("Project name: %s\n" +
                                "      Project Description: %s\n" +
                                "      Project Start Date:%s\n" +
                                "      Project End Date:%s%n", p.getName()
                        , p.getDescription(), p.getStartDate(), p.getEndDate()));
    }

    private static void ex8(EntityManager entityManager) throws IOException {
        int id = Integer.parseInt(READER.readLine());
        Employee employee = entityManager.createQuery("FROM Employee WHERE id=:id ORDER BY firstName", Employee.class)
                .setParameter("id", id)
                .getSingleResult();
        System.out.printf("%s %s %s%n", employee.getFirstName(), employee.getLastName(), employee.getJobTitle());
        employee.getProjects().stream().sorted(Comparator.comparing(Project::getName))
                .forEach(p -> System.out.printf("    %s%n", p.getName()));
    }

    private static void ex7(EntityManager entityManager) {
        entityManager.createQuery("FROM Address ORDER BY employees.size DESC", Address.class)
                .setMaxResults(10)
                .getResultStream()
                .forEach(a ->
                        System.out.printf("%s ,%s  -  %d employees%n", a.getText(), a.getTown().getName(), a.getEmployees().size())
                );
    }

    private static void ex6(EntityManager entityManager) throws IOException {
        Town town = entityManager.find(Town.class, 32);
        Address address = new Address();
        address.setText("Vitoshka 26");
        address.setTown(town);
        entityManager.persist(address);
        String lastName = READER.readLine();
        List<Employee> resultList = entityManager.createQuery("FROM  Employee  WHERE lastName=:last_name", Employee.class)
                .setParameter("last_name", lastName)
                .getResultList();

        if (!resultList.isEmpty()) {
            Employee employee = resultList.get(0);
            employee.setAddress(address);
            entityManager.persist(employee);
        }

    }

    private static void ex5(EntityManager entityManager) {
        List<Employee> resultList = entityManager.createQuery(" SELECT e FROM Employee e  JOIN  e.department d " +
                "WHERE d.name ='Research And Development' ORDER BY e.salary,e.id ", Employee.class).getResultList();


        resultList.forEach(e -> {
            System.out.printf("%s %s from Research and Development - %.2f%n", e.getFirstName(), e.getLastName(), e.getSalary());
        });
    }

    private static void ex4(EntityManager entityManager) {
        List<Employee> resultList = entityManager.createQuery("FROM Employee WHERE salary>50000", Employee.class).getResultList();
        resultList.forEach(e -> {
            System.out.println(e.getFirstName());
        });
    }

    private static void ex2(EntityManager entityManager) {
        List<Town> resultList = entityManager.createQuery("FROM Town WHERE LENGTH(name)>5", Town.class)
                .getResultList();
        resultList.forEach(t -> {
            t.setName(t.getName().toUpperCase());
            entityManager.persist(t);
        });
    }

    private static void ex3(EntityManager entityManager) throws IOException {
        String[] input = READER.readLine().split("\\s+");
        List<Employee> resultList = entityManager.createQuery("FROM Employee WHERE firstName=:first_name AND lastName=:last_name", Employee.class)
                .setParameter("first_name", input[0])
                .setParameter("last_name", input[1])
                .getResultList();

        System.out.println(resultList.size() > 0 ? "Yes" : "No");
    }
}
