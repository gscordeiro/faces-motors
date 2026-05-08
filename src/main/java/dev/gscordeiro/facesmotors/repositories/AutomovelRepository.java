package dev.gscordeiro.facesmotors.repositories;

import java.util.List;
import java.util.Optional;

import jakarta.data.Limit;
import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import dev.gscordeiro.facesmotors.entities.Automovel;
import dev.gscordeiro.facesmotors.entities.Marca;

/**
 * Repositório Jakarta Data para {@link Automovel}.
 *
 * O Hibernate gera a implementação {@code AutomovelRepository_} em tempo de
 * compilação via {@code hibernate-jpamodelgen}. O serviço
 * {@link dev.gscordeiro.facesmotors.services.AutomovelService} atua como
 * intermediário {@code @ApplicationScoped} porque o bean gerado é
 * {@code @Dependent} e mantém uma {@code StatelessSession} não-serializável,
 * o que conflitaria com o {@code @ViewScoped AutomovelBean}.
 */
@Repository
public interface AutomovelRepository extends BasicRepository<Automovel, Long> {

	String JPQL_BASE = "select distinct a from Automovel a "
			+ "join fetch a.modelo "
			+ "join fetch a.cor "
			+ "left join fetch a.fotos ";

	@Query(JPQL_BASE + "order by a.preco desc")
	List<Automovel> listarOrdenadosPorPrecoDesc();

	@Query(JPQL_BASE + "where a.modelo.marca = :marca order by a.preco desc")
	List<Automovel> buscarPorMarca(Marca marca);

	@Query(JPQL_BASE + "where lower(a.modelo.descricao) like :descricao order by a.preco desc")
	List<Automovel> buscarPorDescricao(String descricao);

	@Query(JPQL_BASE + "where a.modelo.marca = :marca and a.modelo.descricao like :descricao order by a.preco desc")
	List<Automovel> buscarPorMarcaEDescricao(Marca marca, String descricao);

	@Query(JPQL_BASE + "where a.id = :id")
	Optional<Automovel> buscarComRelacoes(Long id);

	@Query(JPQL_BASE + "where a.id in :ids")
	List<Automovel> buscarPorIds(List<Long> ids);

	/**
	 * Retorna apenas os ids dos veículos com preço mais próximo do informado,
	 * excluindo o próprio veículo. É feito em duas etapas (ids → entidades)
	 * porque o H2 não aceita {@code ORDER BY} em expressão fora do SELECT
	 * combinada com {@code DISTINCT} + {@code JOIN FETCH}.
	 */
	@Query("select a.id from Automovel a "
			+ "where a.id <> :idAtual and a.modelo.marca = :marca "
			+ "order by case when a.preco >= :preco "
			+ "then a.preco - :preco else :preco - a.preco end")
	List<Long> idsSimilares(Long idAtual, Marca marca, Float preco, Limit limite);
}
