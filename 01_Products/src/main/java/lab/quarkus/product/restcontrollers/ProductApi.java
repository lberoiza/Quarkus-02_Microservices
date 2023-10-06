package lab.quarkus.product.restcontrollers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lab.quarkus.product.entities.Product;
import lab.quarkus.product.repositories.ProductRepositorySpringDataJpa;

import java.util.List;
import java.util.Optional;

@Path("/product")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductApi {

  @Inject
  ProductRepositorySpringDataJpa productRepository;


  @GET
  public List<Product> getProductList() {
    return productRepository.findAll();
  }

  @GET()
  @Path("/{id}")
  public Product getProduct(@PathParam("id") Long id) {
    return productRepository.findById(id).orElseGet(() -> {
      Product p = new Product();
      p.setName("Unknown");
      p.setDescription("Not product found with id: "+id);
      return p;
    });
  }

  @PUT()
  @Path("/{id}")
  public Response updateProduct(@PathParam("id") Long id, Product product) {
    Optional<Product> optionalProduct = productRepository.findById(id);
    if(optionalProduct.isPresent()) {
      Product productToUpdate = optionalProduct.get();
      productToUpdate.updateWith(product);
      productRepository.save(productToUpdate);
    }
    return Response.ok().build();
  }

  @POST
  public Response addProduct(Product product) {
    productRepository.save(product);
    return Response.ok().build();
  }


  @DELETE
  @Path("/{id}")
  public Response deleteProduct(@PathParam("id") Long id) {
    Optional<Product> optionalProduct = productRepository.findById(id);
    optionalProduct.ifPresent(product -> productRepository.delete(product));
    return Response.ok().build();
  }
}
