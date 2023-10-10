package lab.quarkus.customer.repositories;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import lab.quarkus.customer.entities.Customer;

@ApplicationScoped
public class CustomerRepository implements PanacheRepositoryBase<Customer, Long> {
}
