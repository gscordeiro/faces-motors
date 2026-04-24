package dev.gscordeiro.facesmotors.repositories;

import java.util.List;
import java.util.Optional;

import dev.gscordeiro.facesmotors.entities.Automovel;
import dev.gscordeiro.facesmotors.entities.Marca;

/**
 * Contrato do repositório de {@link Automovel}.
 *
 * Originalmente esta interface usava o padrão Jakarta Data
 * ({@code @Repository}, {@code @Query}, {@code BasicRepository}) e o
 * Hibernate gerava a implementação em tempo de compilação. Voltamos para
 * uma implementação manual ({@link AutomovelRepositoryImpl}) porque o
 * combo WildFly Preview 36 + Hibernate 7.0.0.Beta5 sofria uma corrida na
 * inicialização paralela do Weld: o {@code AutomovelRepository_} gerado
 * era processado antes do classloader do deployment indexar entidades
 * referenciadas (como {@link Marca}), o que disparava
 * {@code WELD-000119} de forma não determinística.
 *
 * A interface continua valendo a pena: separa as consultas da lógica de
 * tela e mantém o {@link dev.gscordeiro.facesmotors.services.AutomovelService}
 * acoplado a um contrato, não a JPA.
 */
public interface AutomovelRepository {

	void salvar(Automovel automovel);

	List<Automovel> listarOrdenadosPorPrecoDesc();

	List<Automovel> buscarPorMarca(Marca marca);

	List<Automovel> buscarPorDescricao(String descricaoLike);

	List<Automovel> buscarPorMarcaEDescricao(Marca marca, String descricaoLike);

	Optional<Automovel> buscarComRelacoes(Long id);

	List<Automovel> buscarPorIds(List<Long> ids);

	List<Long> idsSimilares(Long idAtual, Marca marca, Float preco, int limite);
}
