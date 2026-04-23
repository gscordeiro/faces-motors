package dev.gscordeiro.facesmotors.controllers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import org.hibernate.Session;

import dev.gscordeiro.facesmotors.entities.Automovel;
import dev.gscordeiro.facesmotors.entities.Marca;
import dev.gscordeiro.facesmotors.entities.Modelo;
import dev.gscordeiro.facesmotors.security.CodigoService;

/**
 * Backing bean único para todas as telas que falam sobre Automóvel:
 * cadastro/edição, listagem administrativa, vitrine pública e tela de
 * visualização stateless.
 *
 * Mantemos um único bean por entidade para facilitar a leitura no livro,
 * mesmo que isso resulte em um bean um pouco maior. As propriedades estão
 * agrupadas por finalidade.
 */
@Named
@ViewScoped
public class AutomovelBean implements Serializable {

	private static final long serialVersionUID = -8780407253943723401L;

	private static final String JPQL_BASE = "select distinct a from Automovel a "
			+ "join fetch a.modelo "
			+ "join fetch a.modelo.marca "
			+ "join fetch a.cor "
			+ "left join fetch a.fotos ";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private CodigoService codigos;

	// ---- Edição / cadastro -------------------------------------------------
	private Automovel automovel;
	private Marca marca; // utilitário para o combo em cascata Marca -> Modelo

	// ---- Listagem (admin e vitrine) ----------------------------------------
	private List<Automovel> automoveis;

	// ---- Visualização stateless (página pública) ---------------------------
	private String codigo;
	private List<Automovel> sugestoes;

	@PostConstruct
	public void init() {
		automovel = new Automovel();
	}

	// ======================================================================
	// EDIÇÃO
	// ======================================================================

	@Transactional
	public String salvar(Automovel auto) {
		em.persist(auto);
		em.unwrap(Session.class).getSessionFactory().getCache().evictQueryRegion(Automovel.LISTAR_DESTAQUES);

		FacesContext.getCurrentInstance().addMessage(null,
			new FacesMessage("Automóvel salvo com sucesso!"));
		return "listar?faces-redirect=true";
	}

	// ======================================================================
	// LISTAGEM (admin) e VITRINE pública
	// ======================================================================

	public List<Automovel> getAutomoveis() {
		if (automoveis == null) {
			automoveis = em.createQuery(JPQL_BASE + "order by a.preco desc", Automovel.class).getResultList();
		}
		return automoveis;
	}

	public int getTotalAutomoveis() {
		return getAutomoveis().size();
	}

	public void buscarAutomoveis(Modelo modelo) {
		StringBuilder jpql = new StringBuilder(JPQL_BASE).append("where 1=1");
		Map<String, Object> params = new HashMap<>();
		if (modelo.getMarca() != null) {
			jpql.append(" and a.modelo.marca = :marca");
			params.put("marca", modelo.getMarca());
		}
		if (modelo.getDescricao() != null && !modelo.getDescricao().isEmpty()) {
			jpql.append(" and a.modelo.descricao like :descricao");
			params.put("descricao", "%" + modelo.getDescricao() + "%");
		}
		jpql.append(" order by a.preco desc");

		TypedQuery<Automovel> query = em.createQuery(jpql.toString(), Automovel.class);
		params.forEach(query::setParameter);
		automoveis = query.getResultList();
	}

	// ======================================================================
	// VISUALIZAÇÃO PÚBLICA (stateless, bookmarkable)
	//
	// O parâmetro <code>cod</code> da URL é o id do Automovel cifrado por
	// {@link CodigoService}. Aqui decriptamos para localizar o veículo.
	// ======================================================================

	public void carregar() {
		Long id = codigos.decriptar(codigo);
		if (id == null) {
			adicionarAviso("Código de veículo inválido.");
			return;
		}
		try {
			automovel = em.createQuery(JPQL_BASE + "where a.id = :id", Automovel.class)
				.setParameter("id", id)
				.getSingleResult();
		} catch (NoResultException e) {
			adicionarAviso("Veículo não encontrado ou já vendido.");
		}
	}

	public List<Automovel> getSugestoes() {
		if (sugestoes != null) return sugestoes;
		if (!isEncontrado()) {
			sugestoes = List.of();
			return sugestoes;
		}

		// Padrão de duas consultas: o H2 (e o JPA estrito) não permite ORDER BY
		// numa expressão que não esteja no SELECT quando há DISTINCT + JOIN FETCH.
		// Buscamos primeiro só os IDs ordenados pela proximidade de preço e
		// depois carregamos as entidades completas, preservando a ordem em Java.
		List<Long> idsOrdenados = em.createQuery(
				"select a.id from Automovel a "
				+ "where a.id <> :id and a.modelo.marca = :marca "
				+ "order by abs(a.preco - :preco)",
				Long.class)
			.setParameter("id", automovel.getId())
			.setParameter("marca", automovel.getModelo().getMarca())
			.setParameter("preco", automovel.getPreco())
			.setMaxResults(3)
			.getResultList();

		if (idsOrdenados.isEmpty()) {
			sugestoes = List.of();
			return sugestoes;
		}

		Map<Long, Automovel> porId = em.createQuery(JPQL_BASE + "where a.id in :ids", Automovel.class)
			.setParameter("ids", idsOrdenados)
			.getResultList()
			.stream()
			.collect(Collectors.toMap(Automovel::getId, Function.identity()));

		sugestoes = idsOrdenados.stream().map(porId::get).filter(java.util.Objects::nonNull).toList();
		return sugestoes;
	}

	public boolean isEncontrado() {
		return automovel != null && automovel.getId() != null;
	}

	private void adicionarAviso(String texto) {
		FacesContext.getCurrentInstance().addMessage(null,
			new FacesMessage(FacesMessage.SEVERITY_WARN, texto, texto));
	}

	// ======================================================================
	// Getters/Setters
	// ======================================================================

	public Automovel getAutomovel() { return automovel; }
	public void setAutomovel(Automovel automovel) { this.automovel = automovel; }
	public Marca getMarca() { return marca; }
	public void setMarca(Marca marca) { this.marca = marca; }
	public String getCodigo() { return codigo; }
	public void setCodigo(String codigo) { this.codigo = codigo; }
}
