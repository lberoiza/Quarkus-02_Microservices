package lab.quarkus.customer.services;


import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import lab.quarkus.customer.entities.Customer;
import lab.quarkus.customer.entities.Product;
import lab.quarkus.customer.microservices.products.ProductMicroservice;
import lab.quarkus.customer.repositories.CustomerRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@ApplicationScoped
// Sin esta Anotacion no funciona el Servicio
@WithSession
public class CustomerService {

  @Inject
  CustomerRepository customerRepository;

  public Uni<List<Customer>> getCustomerList() {
    return customerRepository.findAll(Sort.by("name")).list();
  }


  public Uni<Customer> getCustomerById(Long id) {
    return customerRepository.findById(id);
  }

  public Uni<Customer> updateCustomer(Long id, Customer customerUpdateData) {
    if (customerUpdateData == null) {
      log.error("Customer data was not include in the request");
      throw new WebApplicationException("Customer was not in the request", HttpResponseStatus.UNPROCESSABLE_ENTITY.code());
    }

    return customerRepository.updateCustomer(id, customerUpdateData);
  }


  public Uni<Customer> addCustomer(Customer customer) {
    return customerRepository.saveCustomer(customer);
  }

  public Uni<Boolean> deleteCustomerById(Long id) {
    return customerRepository.deleteCustomerById(id);

  }

  public Uni<Customer> getCustomerProductsById(Long id, ProductMicroservice productMicroservice) {
    return Uni.combine().all().unis(getReactiveCustomerById(id), productMicroservice.getAllProductsAsMap())
        .combinedWith((reactiveCustomer, allProductsAsMap) -> {
          reactiveCustomer.getProducts().forEach(product -> updateCustomerProduct(product, allProductsAsMap));
          return reactiveCustomer;
        });
  }

  private void updateCustomerProduct(Product customerProduct, Map<Long, Product> allProductsAsMap) {
    if (allProductsAsMap.containsKey(customerProduct.getProductId())) {
      Product productFromMap = allProductsAsMap.get(customerProduct.getProductId());
      customerProduct.setDescription(productFromMap.getDescription());
      customerProduct.setName(productFromMap.getName());
    }
  }

  private Uni<Customer> getReactiveCustomerById(Long id) {
    return customerRepository.findById(id);
  }


}
