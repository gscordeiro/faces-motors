package dev.gscordeiro.facesmotors.controllers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.gscordeiro.facesmotors.repositories.AutomovelRepository;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import org.hibernate.Session;

import dev.gscordeiro.facesmotors.entities.Automovel;
import dev.gscordeiro.facesmotors.entities.Marca;
import dev.gscordeiro.facesmotors.entities.Modelo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Named
@ViewScoped
public class AutomovelBean implements Serializable{
	
	static final long serialVersionUID = -8780407253943723401L;

	private Logger logger = LoggerFactory.getLogger(AutomovelBean.class);
	
	@Inject
	EntityManager em;

	@Inject
	private AutomovelRepository automovelRepository;

	private Automovel automovel;
	private List<Automovel> automoveis;
	private Marca marca; //utilitario para buscar os modelos (combo em cascata)

	@PostConstruct
	public void init(){
		automovel = new Automovel();
	}

	public Automovel getAutomovel() {
		return automovel;
	}

	public void setAutomovel(Automovel automovel) {
		this.automovel = automovel;
	}

	@Transactional
	public String salvar(Automovel auto) {
		em.persist(auto);

		em.unwrap(Session.class).getSessionFactory().getCache().evictQueryRegion(Automovel.LISTAR_DESTAQUES);

		FacesMessage msg = new FacesMessage("Automovel salvo com sucesso!");
		FacesContext.getCurrentInstance().addMessage(null, msg);

		return "listar";
	}

	public void buscarAutomoveis(Modelo modelo){
		String jpql = "select a from Automovel a where 1=1";
		Map<String, Object> params = new HashMap<>();
		if(modelo.getMarca() != null){
			jpql += " and a.modelo.marca = :marca";
			params.put("marca", modelo.getMarca());
		}
		if(modelo.getDescricao() != null && !modelo.getDescricao().isEmpty()){
			jpql += " and a.modelo.descricao like :descricao";
			params.put("descricao", "%" + modelo.getDescricao() + "%");
		}

		TypedQuery<Automovel> query = em.createQuery(jpql, Automovel.class);
		for (Map.Entry<String, Object> param : params.entrySet()) {
			query.setParameter(param.getKey(), param.getValue());
		}
		query.setHint("org.hibernate.cacheable", true);

		automoveis = query.getResultList();
	}

	public List<Automovel> getAutomoveis() {
		if (automoveis == null) {
//			automoveis = em.createNamedQuery(Automovel.LISTAR_DESTAQUES, Automovel.class).getResultList();

			logger.info("Buscando automóveis usando repository {}", automovelRepository.getClass().getSimpleName());
			automoveis = automovelRepository.findAll().toList();
		}

		return automoveis;
	}
	

	public Marca getMarca() {
		return marca;
	}

	public void setMarca(Marca marca) {
		this.marca = marca;
	}
}
