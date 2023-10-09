package lab.quarkus.customer.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "customers")
public class Customer extends PanacheEntity {

  private String code;
  private String accountNumber;
  private String name;
  private String surname;
  private String phone;
  private String address;

  @OneToMany(mappedBy = "customer", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
  @JsonManagedReference
  private List<Product> products;


  public void updateWith(Customer customer) {
    this.setName(customer.getName());
    this.setSurname(customer.getSurname());
    this.setCode(customer.getCode());
    this.setAccountNumber(customer.getAccountNumber());
    this.setPhone(customer.getPhone());
    this.setAddress(customer.getAddress());
  }

}
