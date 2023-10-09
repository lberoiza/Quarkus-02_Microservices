package lab.quarkus.customer.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lab.quarkus.customer.entities.Customer;
import lab.quarkus.customer.entities.Product;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.*;

@Slf4j
@ApplicationScoped
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

  private final PgPool pgPool;

  public CustomerService(PgPool pgPool) {
    this.pgPool = pgPool;
  }

  @PostConstruct
  private void initialize() {
    WebClientOptions microserviceProductOptions = new WebClientOptions()
        .setDefaultHost(microserviceProductHost)
        .setDefaultPort(microserviceProductPort)
        .setSsl(microserviceProductSsl)
        .setTrustAll(microserviceProductTrustAll);
    this.microserviceProductWebClient = WebClient.create(vertx, microserviceProductOptions);
  }


  public Multi<Customer> getCustomerList() {
    return Customer.findAll(pgPool);
  }


  public Uni<Customer> getCustomerById(Long id) {
    return Customer.findById(pgPool, id);
  }

  public Uni<Response> updateCustomer(Customer customer) {
    return customer.update(pgPool).onItem().transform(id -> Response.ok(id).build());
  }

  public Uni<Response> addCustomer(Customer customer) {
    return customer.save(pgPool).onItem().transform(id -> Response.ok(id).build());
  }

  public Uni<Response> deleteCustomer(Long id) {
    return Customer.delete(pgPool, id).onItem().transform(value -> Response.ok().build());
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
//    Optional<Customer> optionalCustomer = customerRepository.findById(id);
//    if (optionalCustomer.isEmpty()) {
//      return Uni.createFrom().nullItem();
//    }
//    return Uni.createFrom().item(optionalCustomer.get());
    return Customer.findById(pgPool, id);
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
      optionalProduct.ifPresent(product -> productMap.put(product.getId(), product));
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
