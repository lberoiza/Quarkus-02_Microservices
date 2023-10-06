package lab.quarkus.customer.repositories;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.TypedQuery;
import lab.quarkus.customer.blazepersistence.entityviews.CustomerEntityView;
import lab.quarkus.customer.entities.Customer;

import java.util.List;

@ApplicationScoped
public class CustomerRepositoryBlaze extends BaseCrud<Customer, Long>{

  @Inject
  CriteriaBuilderFactory criteriaBuilderFactory;

  @Inject
  EntityViewManager entityViewManager;


  @Override
  protected TypedQuery<Customer> getQueryFindById() {
    return em.createQuery("select c from Customer c where c.id = :id", Customer.class);
  }

  @Override
  public List<Customer> findAll() {
//    return em.createQuery("select c from Customer c", Customer.class).getResultList();

    CriteriaBuilder<Customer> criteriaBuilder = criteriaBuilderFactory.create(this.em, Customer.class);
    List<CustomerEntityView> customerEntityViews = entityViewManager
        .applySetting(
            EntityViewSetting.create(CustomerEntityView.class),
            criteriaBuilder
        ).getResultList();

    return customerEntityViews
        .stream()
        .map(customerEntityView -> {
          Customer customer = new Customer();
          customer.setId(customerEntityView.getId());
          customer.updateWith(customerEntityView);
          return customer;
        }).toList();

  }

}
