package lab.quarkus.customer.graphql;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import lab.quarkus.customer.entities.Customer;
import lab.quarkus.customer.services.CustomerService;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;

@GraphQLApi
public class CustomerResource {

  @Inject
  CustomerService customerService;


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
  @Description("Get All Customers from a Database")
  public Uni<List<Customer>> getCustomerList() {
    return customerService.getCustomerList();
  }

}
