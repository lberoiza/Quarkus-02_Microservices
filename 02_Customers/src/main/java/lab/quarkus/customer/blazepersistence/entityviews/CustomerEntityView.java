package lab.quarkus.customer.blazepersistence.entityviews;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import lab.quarkus.customer.entities.Customer;
import lab.quarkus.customer.entities.Product;

import java.util.List;

@EntityView(Customer.class)
public interface CustomerEntityView {

  @IdMapping
  Long getId();

  String getCode();

  String getAccountNumber();

  String getName();

  String getSurname();

  String getPhone();

  String getAddress();

  List<Product> getProducts();
}
