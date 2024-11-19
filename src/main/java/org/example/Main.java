package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.example.entities.Customer;
import org.example.persistence.CustomPersistenceUnitInfo;
import org.hibernate.jpa.HibernatePersistenceProvider;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String puName = "pu-name";
        Map<String,String> props = new HashMap<>();
        props.put("hibernate.show_sql","true");
        props.put("hibernate.hbm2ddl.auto","none");
        EntityManagerFactory emf = new HibernatePersistenceProvider()
                .createContainerEntityManagerFactory(new CustomPersistenceUnitInfo(puName),props);
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Customer> cq = builder.createQuery(Customer.class);

            Root<Customer> customerRoot = cq.from(Customer.class);
            cq.select(customerRoot);

            TypedQuery<Customer> q = em.createQuery(cq);
            q.getResultList().forEach(System.out::println);

            //

            CriteriaQuery<String> cq2 = builder.createQuery(String.class);

            Root<Customer> customerRoot2 = cq2.from(Customer.class);
            cq2.multiselect(customerRoot2.get("name"));

            TypedQuery<String> q2 = em.createQuery(cq2);
            q2.getResultList().forEach(System.out::println);

            CriteriaQuery<Object[]> cq3 = builder.createQuery(Object[].class);

            Root<Customer> customerRoot3 = cq3.from(Customer.class);
            cq3.multiselect(customerRoot3.get("name"),customerRoot3.get("id")).orderBy(builder.desc(customerRoot3.get("id")))  ;

            TypedQuery<Object[]> q3 = em.createQuery(cq3);
            q3.getResultList().forEach(o -> System.out.println(o[0] + " " + o[1]));

            CriteriaQuery<Object[]> cq4 = builder.createQuery(Object[].class);

            Root<Customer> customerRoot4 = cq4.from(Customer.class);
            cq4.multiselect(customerRoot4.get("name"),builder.sum(customerRoot4.get("id")));
            cq4.where(builder.ge(customerRoot4.get("id"),5));
            cq4.groupBy(customerRoot4.get("name"));
            cq4.orderBy(builder.desc(builder.sum(customerRoot4.get("id"))));


            TypedQuery<Object[]> q4 = em.createQuery(cq4);
            q4.getResultList().forEach(o -> System.out.println(o[0] + " " + o[1]));

            em.getTransaction().commit();
        }finally {
            em.close();
        }


    }
}