package dev.gscordeiro.facesmotors.repositories;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import dev.gscordeiro.facesmotors.entities.Automovel;
import dev.gscordeiro.facesmotors.entities.Marca;

/**
 * Implementação JPA do {@link AutomovelRepository}. As queries que
 * carregam o agregado completo (modelo, marca, cor, fotos) usam
 * {@code join fetch} para evitar {@code LazyInitializationException}
 * quando a view renderiza fora da transação.
 */
@ApplicationScoped
public class AutomovelRepositoryImpl implements AutomovelRepository {

	/**
	 * Trecho reaproveitado pelos métodos que precisam carregar o agregado
	 * completo numa única query. O {@code distinct} é necessário porque os
	 * {@code join fetch} em coleções produzem linhas duplicadas.
	 */
	private static final String JPQL_BASE = "select distinct a from Automovel a "
			+ "join fetch a.modelo "
			+ "join fetch a.cor "
			+ "left join fetch a.fotos ";

	@PersistenceContext
	private EntityManager em;

	@Override
	public void salvar(Automovel automovel) {
		if (automovel.getId() == null) {
			em.persist(automovel);
		} else {
			em.merge(automovel);
		}
	}

	@Override
	public List<Automovel> listarOrdenadosPorPrecoDesc() {
		return em.createQuery(JPQL_BASE + "order by a.preco desc", Automovel.class)
				.getResultList();
	}

	@Override
	public List<Automovel> buscarPorMarca(Marca marca) {
		return em.createQuery(JPQL_BASE + "where a.modelo.marca = :marca order by a.preco desc",
				Automovel.class)
				.setParameter("marca", marca)
				.getResultList();
	}

	@Override
	public List<Automovel> buscarPorDescricao(String descricaoLike) {
		return em.createQuery(JPQL_BASE + "where a.modelo.descricao like :descricao order by a.preco desc",
				Automovel.class)
				.setParameter("descricao", descricaoLike)
				.getResultList();
	}

	@Override
	public List<Automovel> buscarPorMarcaEDescricao(Marca marca, String descricaoLike) {
		return em.createQuery(JPQL_BASE
						+ "where a.modelo.marca = :marca and a.modelo.descricao like :descricao "
						+ "order by a.preco desc",
				Automovel.class)
				.setParameter("marca", marca)
				.setParameter("descricao", descricaoLike)
				.getResultList();
	}

	@Override
	public Optional<Automovel> buscarComRelacoes(Long id) {
		try {
			return Optional.of(em.createQuery(JPQL_BASE + "where a.id = :id", Automovel.class)
					.setParameter("id", id)
					.getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	@Override
	public List<Automovel> buscarPorIds(List<Long> ids) {
		return em.createQuery(JPQL_BASE + "where a.id in :ids", Automovel.class)
				.setParameter("ids", ids)
				.getResultList();
	}

	/**
	 * Retorna apenas os ids dos veículos com preço mais próximo do informado,
	 * excluindo o próprio veículo. É feito em duas etapas (ids → entidades)
	 * porque o H2 não aceita {@code ORDER BY} em expressão fora do SELECT
	 * combinada com {@code DISTINCT} + {@code JOIN FETCH}.
	 */
	@Override
	public List<Long> idsSimilares(Long idAtual, Marca marca, Float preco, int limite) {
		return em.createQuery(
						"select a.id from Automovel a "
								+ "where a.id <> :idAtual and a.modelo.marca = :marca "
								+ "order by case when a.preco >= :preco "
								+ "then a.preco - :preco else :preco - a.preco end",
						Long.class)
				.setParameter("idAtual", idAtual)
				.setParameter("marca", marca)
				.setParameter("preco", preco)
				.setMaxResults(limite)
				.getResultList();
	}
}
