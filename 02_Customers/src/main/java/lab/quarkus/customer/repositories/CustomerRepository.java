package lab.quarkus.customer.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import lab.quarkus.customer.entities.Customer;

import java.util.List;

@ApplicationScoped
public class CustomerRepository extends BaseCrud<Customer, Long>{

  @Override
  protected TypedQuery<Customer> getQueryFindById() {
    return em.createQuery("select c from Customer c where c.id = :id", Customer.class);
  }

  @Override
  public List<Customer> getAll() {
    return em.createQuery("select c from Customer c", Customer.class).getResultList();
  }

}
