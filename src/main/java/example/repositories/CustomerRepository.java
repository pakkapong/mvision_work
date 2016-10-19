package example.repositories;


import example.Model.Customer;
import org.springframework.data.repository.CrudRepository;


public interface CustomerRepository extends CrudRepository<Customer, String> {

     //Customer save(Customer customer);
}