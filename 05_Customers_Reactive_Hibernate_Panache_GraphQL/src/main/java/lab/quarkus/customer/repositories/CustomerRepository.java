package lab.quarkus.customer.repositories;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lab.quarkus.customer.entities.Customer;

@ApplicationScoped
public class CustomerRepository implements PanacheRepositoryBase<Customer, Long> {

  public Uni<Customer> saveCustomer(Customer customer) {
    customer.getProducts().forEach(product -> product.setCustomer(customer));
    return Panache.withTransaction(() -> this.persist(customer));
  }

  public Uni<Customer> updateCustomer(Long id, Customer customerUpdateData) {
    return Panache.withTransaction(() -> this.findById(id)
        .onItem().ifNotNull()
        .invoke(foundedCustomer -> foundedCustomer.updateWith(customerUpdateData))
    );
  }

  public Uni<Boolean> deleteCustomerById(Long id) {
    return Panache.withTransaction(() -> this.deleteById(id));
  }

}
