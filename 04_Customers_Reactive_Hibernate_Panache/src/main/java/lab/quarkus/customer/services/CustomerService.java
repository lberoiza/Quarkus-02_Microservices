package lab.quarkus.customer.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lab.quarkus.customer.entities.Customer;
import lab.quarkus.customer.entities.Product;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.*;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;
import static jakarta.ws.rs.core.Response.Status.CREATED;

@Slf4j
@ApplicationScoped
// Sin esta Anotacion no funciona el Servicio
@WithSession
public class CustomerService {

  @ConfigProperty(name = "microservice.products.host")
  String microserviceProductHost;

  @ConfigProperty(name = "microservice.products.port")
  Integer microserviceProductPort;

  @ConfigProperty(name = "microservice.products.ssl")
  Boolean microserviceProductSsl;

  @ConfigProperty(name = "microservice.products.trustAll")
  Boolean microserviceProductTrustAll;

  @Inject
  Vertx vertx;

  private WebClient microserviceProductWebClient;

  @PostConstruct
  private void initialize() {
    WebClientOptions microserviceProductOptions = new WebClientOptions()
        .setDefaultHost(microserviceProductHost)
        .setDefaultPort(microserviceProductPort)
        .setSsl(microserviceProductSsl)
        .setTrustAll(microserviceProductTrustAll);
    this.microserviceProductWebClient = WebClient.create(vertx, microserviceProductOptions);
  }


  public Uni<List<PanacheEntityBase>> getCustomerList() {
    return Customer.listAll(Sort.by("name"));
  }


  public Uni<PanacheEntityBase> getCustomerById(Long id) {
    return Customer.findById(id);
  }

  public Uni<Response> updateCustomer(Long id, Customer customerUpdateData) {
    if (customerUpdateData == null) {
      log.error("Customer data was not include in the request");
      throw new WebApplicationException("Customer was not in the request", HttpResponseStatus.UNPROCESSABLE_ENTITY.code());
    }

    return Panache.withTransaction(() ->
            Customer
                .<Customer>findById(id)
                .onItem().ifNotNull()
                .invoke(entity -> entity.updateWith(customerUpdateData))
        ).onItem().ifNotNull()
        .transform(entity -> Response.ok(entity).build())
        .onItem().ifNull()
        .continueWith(Response.ok().status(NOT_FOUND).build());
  }

  public Uni<Response> addCustomer(Customer customer) {
    customer.getProducts().forEach(product -> product.setCustomer(customer));
    return Panache
        .withTransaction(customer::persist)
        .replaceWith(
            Response.ok(customer).status(CREATED)::build
        );
  }

  public Uni<Response> deleteCustomer(Long id) {
    return Panache.withTransaction(() -> Customer.deleteById(id))
        .map(deleted -> {
          Response.Status deleteStatus = deleted ? NO_CONTENT : NOT_FOUND;
          return Response.ok().status(deleteStatus).build();
        });
  }

  public Uni<Customer> getCustomerProductsById(Long id) {
    return Uni.combine().all().unis(getReactiveCustomerById(id), getFromMicroserviceProductsAllProducts())
        .combinedWith((reactiveCustomer, allProductsAsMap) -> {
          reactiveCustomer.getProducts().forEach(product -> updateCustomerProduct(product, allProductsAsMap));
          return reactiveCustomer;
        });
  }

  private void updateCustomerProduct(Product customerProduct, Map<Long, Product> allProductsAsMap) {
    if (allProductsAsMap.containsKey(customerProduct.getId())) {
      Product productFromMap = allProductsAsMap.get(customerProduct.getId());
      customerProduct.setDescription(productFromMap.getDescription());
      customerProduct.setName(productFromMap.getName());
    }
  }

  private Uni<Customer> getReactiveCustomerById(Long id) {
    return Customer.findById(id);
  }


  private Uni<Map<Long, Product>> getFromMicroserviceProductsAllProducts() {
    return microserviceProductWebClient.get("/product")
        .send()
        .onFailure().invoke(this::failureOnMicroservice)
        .onItem().transform(this::transformResponse);
  }

  private Map<Long, Product> transformResponse(HttpResponse<Buffer> response) {
    Map<Long, Product> productMap = new HashMap<>();
    JsonArray objects = response.bodyAsJsonArray();
    log.info("Getting from Microservice List Products as json: {}", objects.toString());
    objects.forEach(object -> {
      Optional<Product> optionalProduct = parseJsonObjectToProduct(object.toString());
      optionalProduct.ifPresent(product -> productMap.put(product.getProduct(), product));
    });
    return productMap;
  }

  private Optional<Product> parseJsonObjectToProduct(String jsonObject) {
    ObjectMapper objectMapper = new ObjectMapper();
    log.info("processing: Json Object {}", jsonObject);
    try {
      return Optional.of(objectMapper.readValue(jsonObject, Product.class));
    } catch (JsonProcessingException jpe) {
      log.error("Error to parse Json Object to product");
    }
    return Optional.empty();
  }

  private void failureOnMicroservice(Throwable error) {
    log.error("Error by getting Products from Microservices: '{}'", error.getMessage());
  }

}
