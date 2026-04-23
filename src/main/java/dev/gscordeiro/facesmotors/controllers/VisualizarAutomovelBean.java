package dev.gscordeiro.facesmotors.controllers;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import dev.gscordeiro.facesmotors.entities.Automovel;

/**
 * Backing bean stateless da página /automovel/visualizar.xhtml.
 *
 * Recebe o "código bookmarkable" do veículo via {@code <f:viewParam>} e carrega
 * o automóvel a partir desse código por meio de uma {@code <f:viewAction>}.
 * Como a tela é stateless ({@code <f:view transient="true">}), a cada GET o JSF:
 *
 *   1. Lê o parâmetro <code>cod</code> da URL e injeta em {@link #codigo};
 *   2. Dispara a action {@link #carregar()};
 *   3. Renderiza a view com o {@link #automovel} resolvido.
 */
@Named
@RequestScoped
public class VisualizarAutomovelBean {

	@PersistenceContext
	private EntityManager em;

	private String codigo;
	private Automovel automovel;
	private List<Automovel> sugestoes;

	public void carregar() {
		if (codigo == null || codigo.isBlank()) {
			adicionarMensagem("Informe o código do veículo.");
			return;
		}
		try {
			automovel = em.createQuery(
					"select a from Automovel a "
					+ "join fetch a.modelo "
					+ "join fetch a.modelo.marca "
					+ "join fetch a.cor "
					+ "left join fetch a.fotos "
					+ "where a.codigo = :cod",
					Automovel.class)
				.setParameter("cod", codigo.toUpperCase())
				.getSingleResult();
		} catch (NoResultException e) {
			adicionarMensagem("Veículo não encontrado ou já vendido.");
		}
	}

	public List<Automovel> getSugestoes() {
		if (sugestoes == null) {
			if (automovel == null) {
				sugestoes = List.of();
			} else {
				sugestoes = em.createQuery(
						"select a from Automovel a "
						+ "join fetch a.modelo "
						+ "join fetch a.modelo.marca "
						+ "join fetch a.cor "
						+ "left join fetch a.fotos "
						+ "where a.id <> :id and a.modelo.marca = :marca "
						+ "order by abs(a.preco - :preco)",
						Automovel.class)
					.setParameter("id", automovel.getId())
					.setParameter("marca", automovel.getModelo().getMarca())
					.setParameter("preco", automovel.getPreco())
					.setMaxResults(3)
					.getResultList();
			}
		}
		return sugestoes;
	}

	private void adicionarMensagem(String texto) {
		FacesContext.getCurrentInstance().addMessage(null,
			new FacesMessage(FacesMessage.SEVERITY_WARN, texto, texto));
	}

	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public Automovel getAutomovel() {
		return automovel;
	}
	public boolean isEncontrado() {
		return automovel != null;
	}
}
