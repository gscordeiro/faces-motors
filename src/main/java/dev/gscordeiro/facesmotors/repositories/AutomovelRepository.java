package dev.gscordeiro.facesmotors.repositories;

import dev.gscordeiro.facesmotors.entities.Automovel;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

@Repository
public interface AutomovelRepository extends CrudRepository<Automovel, Long> {
}
