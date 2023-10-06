package lab.quarkus.customer.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lab.quarkus.customer.blazepersistence.entityviews.CustomerEntityView;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "customers")
public class Customer implements CustomerEntityView {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String code;
  private String accountNumber;
  private String name;
  private String surname;
  private String phone;
  private String address;

  @OneToMany(mappedBy = "customer", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
  @JsonManagedReference
  private List<Product> products;


  public void updateWith(CustomerEntityView customerEntityView) {
    this.setName(customerEntityView.getName());
    this.setSurname(customerEntityView.getSurname());
    this.setCode(customerEntityView.getCode());
    this.setAccountNumber(customerEntityView.getAccountNumber());
    this.setPhone(customerEntityView.getPhone());
    this.setAddress(customerEntityView.getAddress());
  }

}
