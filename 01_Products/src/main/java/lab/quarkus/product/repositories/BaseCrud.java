package lab.quarkus.product.repositories;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

public abstract class BaseCrud<E, K> {

  @Inject
  protected EntityManager em;


  protected abstract TypedQuery<E> getQueryFindById();


  @Transactional
  public void save(E entity) {
    em.persist(entity);
  }

  @Transactional
  public void update(E entity) {
    em.merge(entity);
  }

  @Transactional
  public void delete(E entity) {
    em.remove(entity);
  }

  @Transactional
  public abstract List<E> getAll();

  @Transactional
  public Optional<E> findById(K id) {
    E entity = getQueryFindById()
        .setParameter("id", id)
        .getSingleResult();

    if (entity == null) {
      return Optional.empty();
    }

    return Optional.of(entity);
  }
}
