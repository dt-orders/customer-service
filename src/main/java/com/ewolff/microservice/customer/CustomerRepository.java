package com.ewolff.microservice.customer;

import java.util.List;
//import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "customer", path = "customer")//, excerptProjection = Customer.class)
public interface CustomerRepository extends CrudRepository<Customer, Long> {

	List<Customer> findByName(@Param("name") String name);
	//List<Customer> findById(@Param("id") long id);

}
