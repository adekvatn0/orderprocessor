package io.ambershogun.repository;

import io.ambershogun.request.entity.Request;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface RequestRepository extends CrudRepository<Request, Long> { }
