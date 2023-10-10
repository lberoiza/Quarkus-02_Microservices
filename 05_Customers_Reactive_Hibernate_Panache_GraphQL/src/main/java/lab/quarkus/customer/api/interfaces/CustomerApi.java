package lab.quarkus.customer.api.interfaces;

import io.smallrye.mutiny.Uni;
import lab.quarkus.customer.entities.Customer;

import java.util.List;


public interface CustomerApi {

  Uni<List<Customer>> getCustomerList();

  Uni<Customer> getCustomer(Long id);

  Uni<Customer> getCustomerProducts(Long id);

  Uni<?> updateCustomer(Long id, Customer customer);

  Uni<?> addCustomer(Customer customer);

  Uni<?> deleteCustomer(Long id);


}
