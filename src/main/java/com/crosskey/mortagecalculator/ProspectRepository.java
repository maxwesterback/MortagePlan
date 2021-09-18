package com.crosskey.mortagecalculator;

import org.springframework.data.repository.CrudRepository;

public interface ProspectRepository extends CrudRepository<Prospect, Integer> {

    Prospect findCustomerById(Integer id);
}
