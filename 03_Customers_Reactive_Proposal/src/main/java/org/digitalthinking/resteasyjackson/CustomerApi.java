package org.digitalthinking.resteasyjackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;

import io.vertx.mutiny.pgclient.PgPool;
import lombok.extern.slf4j.Slf4j;
import org.digitalthinking.entites.Customer;
import org.digitalthinking.entites.Product;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class CustomerApi {

  @Inject
  Vertx vertx;

  private WebClient webClient;

  private final PgPool client;

  public CustomerApi(PgPool client) {
    this.client = client;
  }

  @PostConstruct
  void initialize() {
    this.webClient = WebClient.create(vertx,
        new WebClientOptions().setDefaultHost("localhost")
            .setDefaultPort(8081).setSsl(false).setTrustAll(true));
  }


  @GET
  public Multi<Customer> list() {
    return Customer.findAll(client);
  }

  @GET
  @Path("/{Id}")
  public Uni<Customer> getById(@PathParam("Id") Long Id) {
    return Customer.findById(client, Id);
  }

  @GET
  @Path("/{Id}/product")
  public Uni<Customer> getByIdProduct(@PathParam("Id") Long Id) {
    return Uni.combine().all().unis(getCustomerReactive(Id), getAllProducts())
        .combinedWith((v1, v2) -> {
          v1.getProducts().forEach(product -> v2.forEach(p -> {
            if (product.getId().equals(p.getId())) {
              product.setName(p.getName());
              product.setDescription(p.getDescription());
            }
          }));
          return v1;
        });
  }

  @POST
  public Uni<Response> add(Customer c) {
    return c.save(client).onItem().transform(id -> Response.ok(id).build());
  }

  @DELETE
  @Path("/{Id}")
  public Uni<Boolean> delete(@PathParam("Id") Long Id) {
    return Customer.delete(client, Id);
  }

  @PUT
  public Uni<Response> update(Customer c) {
    return c.update(client).onItem().transform(id -> Response.ok(id).build());
  }


  private Uni<Customer> getCustomerReactive(Long Id) {
    return Customer.findById(client, Id);
  }

  private Uni<List<Product>> getAllProducts() {
    return webClient.get(8081, "localhost", "/product").send()
        .onFailure().invoke(res -> log.error("Error recuperando productos ", res))
        .onItem().transform(res -> {
          List<Product> lista = new ArrayList<>();
          JsonArray objects = res.bodyAsJsonArray();
          objects.forEach(p -> {
            log.info("See Objects: " + objects);
            ObjectMapper objectMapper = new ObjectMapper();
            // Pass JSON string and the POJO class
            Product product = null;
            try {
              product = objectMapper.readValue(p.toString(), Product.class);
            } catch (JsonProcessingException e) {
              log.error(e.getMessage());
            }
            lista.add(product);
          });
          return lista;
        });
  }


}
