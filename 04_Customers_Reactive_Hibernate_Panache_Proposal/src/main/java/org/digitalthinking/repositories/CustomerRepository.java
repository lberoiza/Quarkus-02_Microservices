package org.digitalthinking.repositories;


import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import org.digitalthinking.entites.Customer;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomerRepository implements PanacheRepositoryBase<Customer,Long> {

}
