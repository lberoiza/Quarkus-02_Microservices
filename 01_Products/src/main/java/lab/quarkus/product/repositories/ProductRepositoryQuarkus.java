package lab.quarkus.product.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lab.quarkus.product.entities.Product;

import java.util.List;

@ApplicationScoped
public class ProductRepositoryQuarkus extends BaseCrud<Product, Long>{

  @Inject
  EntityManager em;

  @Override
  protected TypedQuery<Product> getQueryFindById() {
    return em.createQuery("select p from Product p where p.id = :id", Product.class);
  }

  @Override
  public List<Product> findAll() {
    return em.createQuery("select p from Product p", Product.class).getResultList();
  }

}
