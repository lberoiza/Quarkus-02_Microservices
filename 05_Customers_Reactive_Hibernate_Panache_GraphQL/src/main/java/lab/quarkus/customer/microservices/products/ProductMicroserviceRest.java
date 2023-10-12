package lab.quarkus.customer.microservices.products;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lab.quarkus.customer.entities.Product;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.*;

@Slf4j
@ApplicationScoped
@Named("ProductMicroserviceRest")
public class ProductMicroserviceRest implements ProductMicroservice{


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


  @Override
  public Uni<List<Product>> getAllProductsAsList() {
    return this.getAllProducts()
        .onItem().transform(this::httpResponseToProductList);
  }

  @Override
  public Uni<Map<Long, Product>> getAllProductsAsMap() {
    return this.getAllProducts()
        .onItem().transform(this::httpResponseToProductListMap);
  }

  @Override
  public Uni<Product> getProductById(Long id) {
    return microserviceProductWebClient.get("/product/"+id)
        .send()
        .onFailure().invoke(this::failureOnMicroservice)
        .onItem().transform(this::httpResponseToProduct);
  }

  private Uni<HttpResponse<Buffer>> getAllProducts(){
    return microserviceProductWebClient.get("/product")
        .send()
        .onFailure().invoke(this::failureOnMicroservice);
  }

  private Product httpResponseToProduct(HttpResponse<Buffer> response) {
    JsonObject jsonObject = response.bodyAsJsonObject();
    Optional<Product> optionalProduct = parseJsonObjectToProduct(jsonObject.toString());
    return optionalProduct.get();
  }

  private List<Product> httpResponseToProductList(HttpResponse<Buffer> response){
    List<Product> productList = new ArrayList<>();
    JsonArray objects = response.bodyAsJsonArray();
    log.info("Getting from Microservice List Products as json: {}", objects.toString());
    objects.forEach(object -> {
      Optional<Product> optionalProduct = parseJsonObjectToProduct(object.toString());
      optionalProduct.ifPresent(product -> {
        product.setProductId(product.id);
        productList.add(product);
      });
    });

    return productList;
  }

  private Map<Long, Product> httpResponseToProductListMap(HttpResponse<Buffer> response) {
    Map<Long, Product> productMap = new HashMap<>();
    List<Product> productList = httpResponseToProductList(response);
    productList.forEach(product -> productMap.put(product.getProductId(), product));
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
