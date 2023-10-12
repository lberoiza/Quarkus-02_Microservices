package lab.quarkus.customer.api.graphql;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lab.quarkus.customer.api.interfaces.CustomerApi;
import lab.quarkus.customer.entities.Customer;
import lab.quarkus.customer.entities.Product;
import lab.quarkus.customer.microservices.products.ProductMicroservice;
import lab.quarkus.customer.services.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.graphql.*;

import java.util.List;

@Slf4j
@GraphQLApi
public class CustomerGraphQl implements CustomerApi {

  @Inject
  CustomerService customerService;

  @Inject
  @Named("ProductMicroserviceGraphQl")
  ProductMicroservice productMicroservice;


  // Si no se pone un nombre, toma el nombre del método
  // sin el get del principio (customerList)

  // Ejemplo
  // http://localhost:8085/q/graphql-ui
  // =====================================
  //  query getAllCustomers {
  //    allCustomers{
  //      id,
  //          name,
  //          accountNumber,
  //          phone
  //    }
  //  }

  // =====================================
  // Obtiene
  //  {
  //    "data": {
  //    "allCustomers": [
  //    {
  //      "id": 1,
  //        "name": "Luis Alberto",
  //        "accountNumber": "987654321",
  //        "phone": "123546"
  //    },
  //    {
  //      "id": 2,
  //        "name": "Silvia",
  //        "accountNumber": "654741369",
  //        "phone": "987456"
  //    }
  //    ]
  //  }
  //  }
  @Query("allCustomers")
  @Description("Get All Customers from the Database")
  @Override
  public Uni<List<Customer>> getCustomerList() {
    return customerService.getCustomerList();
  }

  @Query("customer")
  @Description("Get Customer from the Database")
  @Override
  public Uni<Customer> getCustomer(@Name("customerId") Long id) {
    return customerService.getCustomerById(id);
  }

  @Query("customerProducts")
  @Description("Get the Information of a Customer and his products from Product-Microservice")
  @Override
  public Uni<Customer> getCustomerProducts(@Name("customerId") Long id) {
    return customerService.getCustomerProductsById(id, productMicroservice);
  }

  @Mutation("updateCustomer")
  @Description("Update a Customer with Id and Customer Data")
  @Override
  public Uni<Customer> updateCustomer(@Name("customerId") Long id, @Name("customerData") Customer customer) {
    return customerService.updateCustomer(id, customer);
  }


  //  mutation create{
  //    addCustomer(customerData: {
  //      name: "Tess",
  //          accountNumber: "753159",
  //          address: "Smallvile",
  //          code: "TSL",
  //          surname: "Mercer",
  //          phone: "456789",
  //          products: [
  //      {
  //        product: 1
  //      },
  //      {
  //        product: 3
  //      }
  //    ]
  //    }){
  //      surname,
  //          name,
  //          id
  //    }
  //
  //  }
  @Mutation("addCustomer")
  @Description("Create a new Customer")
  @Override
  public Uni<Customer> addCustomer(@Name("customerData") Customer customer) {
    return customerService.addCustomer(customer);
  }


  //  mutation deleteCustomer{
  //    deleteCustomer(customerId: 4)
  //  }
  @Mutation("deleteCustomer")
  @Description("Delete a customer")
  @Override
  public Uni<Boolean> deleteCustomerById(@Name("customerId") Long id) {
    return customerService.deleteCustomerById(id);
  }

  @Override
  @Query("productById")
  @Description("Get A Product by Id from Product Microservice")
  public Uni<Product> getProductById(@Name("productId") Long id) {
    log.info("GraphQl Getting from Product Microservice Information by Id: {}", id);
    return productMicroservice.getProductById(id);
  }

  @Override
  @Query("productList")
  @Description("Get the Product List from Product Microservice")
  public Uni<List<Product>> getAllProducts() {
    return productMicroservice.getAllProductsAsList();
  }


}
