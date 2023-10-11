package lab.quarkus.product.services;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lab.quarkus.product.entities.Product;
import lab.quarkus.product.repositories.ProductRepository;

import java.util.List;

@ApplicationScoped
@WithSession
public class ProductService {

  @Inject
  ProductRepository productRepository;

  public Uni<List<Product>> getProductList() {
    return productRepository.listAll();
  }

  public Uni<Product> getProduct(Long id) {
    return productRepository.findById(id);
  }

  public Uni<Product> updateProduct(Long id, Product product) {
    return productRepository.updateProduct(id, product);
  }

  public Uni<Product> addProduct(Product product) {
    return productRepository.addProduct(product);
  }


  public Uni<Boolean> deleteProductById(Long id){
    return productRepository.deleteProductById(id);
  }

}
