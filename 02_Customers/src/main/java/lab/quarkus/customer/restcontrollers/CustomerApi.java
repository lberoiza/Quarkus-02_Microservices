package lab.quarkus.customer.restcontrollers;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lab.quarkus.customer.entities.Customer;
import lab.quarkus.customer.services.CustomerService;

import java.util.List;

@Path("/customer")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CustomerApi {

  @Inject
  CustomerService customerService;


  @GET
  public List<Customer> getCustomerList() {
    return customerService.getCustomerList();
  }

  @GET()
  @Path("/{id}")
  public Customer getCustomer(@PathParam("id") Long id) {
    return customerService.getCustomerById(id);
  }


  @GET()
  @Path("/{id}/products")
  // Se debe Usar Blocking, porque lamentablemente esta llamada de servicio
  // no es completamente reactiva, debido a que la base de datos H2 no soporta
  // reactividad, por eso se debe usar @Blocking, para que este servicio
  // sea tratado como un servicio bloqueante
  @Blocking
  public Uni<Customer> getCustomerProducts(@PathParam("id") Long id) {
    return customerService.getCustomerProductsById(id);
  }

  @PUT()
  @Path("/{id}")
  public Response updateCustomer(@PathParam("id") Long id, Customer customer) {
    customerService.updateCustomer(id, customer);
    return Response.ok().build();
  }


  @POST
  public Response addCustomer(Customer customer) {
    customerService.addCustomer(customer);
    return Response.ok().build();
  }


  @DELETE
  @Path("/{id}")
  public Response deleteProduct(@PathParam("id") Long id) {
    customerService.deleteCustomer(id);
    return Response.ok().build();
  }
}
