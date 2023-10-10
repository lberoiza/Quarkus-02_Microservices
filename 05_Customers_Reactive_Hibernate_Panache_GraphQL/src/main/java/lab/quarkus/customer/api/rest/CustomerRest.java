package lab.quarkus.customer.api.rest;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lab.quarkus.customer.entities.Customer;
import lab.quarkus.customer.services.CustomerService;

import java.util.List;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;
import static jakarta.ws.rs.core.Response.Status.CREATED;

@Path("/customer")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CustomerRest {

  @Inject
  CustomerService customerService;


  @GET
  public Uni<List<Customer>> getCustomerList() {
    return customerService.getCustomerList();
//    return Customer.findAll().list();
  }

  @GET()
  @Path("/{id}")
  public Uni<Customer> getCustomer(@PathParam("id") Long id) {
    return customerService.getCustomerById(id);
  }


  @GET()
  @Path("/{id}/products")
  public Uni<Customer> getCustomerProducts(@PathParam("id") Long id) {
    return customerService.getCustomerProductsById(id);
  }

  @PUT()
  @Path("/{id}")
  public Uni<Response> updateCustomer(@PathParam("id") Long id, Customer customer) {
    return customerService.updateCustomer(id, customer)
        .onItem().ifNotNull()
        .transform(entity -> Response.ok(entity).build())
        .onItem().ifNull()
        .continueWith(Response.ok().status(NOT_FOUND).build());
  }


  @POST
  public Uni<Response> addCustomer(Customer customer) {
    return customerService.addCustomer(customer)
        .replaceWith(Response.ok(customer).status(CREATED)::build);
  }


  @DELETE
  @Path("/{id}")
  public Uni<Response> deleteProduct(@PathParam("id") Long id) {
    return customerService.deleteCustomer(id)
        .map(deleted -> {
          Response.Status deleteStatus = deleted ? NO_CONTENT : NOT_FOUND;
          return Response.ok().status(deleteStatus).build();
        });
  }
}
