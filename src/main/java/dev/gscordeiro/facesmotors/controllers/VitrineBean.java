package dev.gscordeiro.facesmotors.controllers;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import dev.gscordeiro.facesmotors.entities.Automovel;

@Named
@RequestScoped
public class VitrineBean {

	@PersistenceContext
	private EntityManager em;

	private List<Automovel> automoveis;

	public List<Automovel> getAutomoveis() {
		if (automoveis == null) {
			automoveis = em.createQuery(
					"select distinct a from Automovel a "
					+ "join fetch a.modelo "
					+ "join fetch a.modelo.marca "
					+ "join fetch a.cor "
					+ "left join fetch a.fotos "
					+ "order by a.preco desc",
					Automovel.class).getResultList();
		}
		return automoveis;
	}

	public int getTotalAutomoveis() {
		return getAutomoveis().size();
	}
}
