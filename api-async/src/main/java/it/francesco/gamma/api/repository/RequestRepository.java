package it.francesco.gamma.api.repository;

import it.francesco.gamma.api.model.DbRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestRepository extends CrudRepository<DbRequest, String> {

}
