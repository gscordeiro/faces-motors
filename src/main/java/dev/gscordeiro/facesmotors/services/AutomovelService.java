package dev.gscordeiro.facesmotors.services;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import dev.gscordeiro.facesmotors.entities.Automovel;
import dev.gscordeiro.facesmotors.entities.Marca;
import dev.gscordeiro.facesmotors.entities.Modelo;
import dev.gscordeiro.facesmotors.repositories.AutomovelRepository;

/**
 * Camada de serviço para {@link Automovel}.
 *
 * Concentra transações e a orquestração de consultas (filtros condicionais,
 * busca de sugestões em duas etapas). O repositório expõe queries cruas; o
 * controller só conhece operações de domínio.
 *
 * É {@code @ApplicationScoped} (escopo normal) para que o CDI injete um
 * proxy serializável no {@code @ViewScoped AutomovelBean}.
 */
@ApplicationScoped
public class AutomovelService {

	@Inject
	private AutomovelRepository repositorio;

	@Transactional
	public void salvar(Automovel auto) {
		repositorio.salvar(auto);
	}

	public List<Automovel> listar() {
		return repositorio.listarOrdenadosPorPrecoDesc();
	}

	/**
	 * Aplica os filtros opcionais de marca e descrição. Cada combinação cai
	 * numa query JPQL específica do repositório.
	 */
	public List<Automovel> buscar(Modelo filtro) {
		Marca marca = filtro.getMarca();
		String descricao = filtro.getDescricao();
		boolean temDescricao = descricao != null && !descricao.isEmpty();
		String descricaoLike = temDescricao ? "%" + descricao + "%" : null;

		if (marca != null && temDescricao) {
			return repositorio.buscarPorMarcaEDescricao(marca, descricaoLike);
		}
		if (marca != null) {
			return repositorio.buscarPorMarca(marca);
		}
		if (temDescricao) {
			return repositorio.buscarPorDescricao(descricaoLike);
		}
		return repositorio.listarOrdenadosPorPrecoDesc();
	}

	public Optional<Automovel> buscarComRelacoes(Long id) {
		return repositorio.buscarComRelacoes(id);
	}

	/**
	 * Sugere até 3 veículos da mesma marca, ordenados pela proximidade do
	 * preço. Faz duas queries (ids → entidades) porque o H2 recusa
	 * {@code ORDER BY} em expressão fora do SELECT quando combinado com
	 * {@code DISTINCT} + {@code JOIN FETCH}.
	 */
	public List<Automovel> sugestoesPara(Automovel atual) {
		List<Long> idsOrdenados = repositorio.idsSimilares(
				atual.getId(),
				atual.getModelo().getMarca(),
				atual.getPreco(),
				3);

		if (idsOrdenados.isEmpty()) {
			return List.of();
		}

		Map<Long, Automovel> porId = repositorio.buscarPorIds(idsOrdenados).stream()
				.collect(Collectors.toMap(Automovel::getId, Function.identity()));

		return idsOrdenados.stream().map(porId::get).filter(Objects::nonNull).toList();
	}
}
