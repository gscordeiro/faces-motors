package br.com.casadocodigo.jsfjpa.controllers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import br.com.casadocodigo.jsfjpa.entities.Automovel;
import br.com.casadocodigo.jsfjpa.entities.Marca;
import br.com.casadocodigo.jsfjpa.entities.Modelo;
import br.com.casadocodigo.jsfjpa.persistence.JpaUtil;


@ManagedBean
@ViewScoped
public class AutomovelBean implements Serializable{
	
	static final long serialVersionUID = -8780407253943723401L;
	
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

	public String salvar(Automovel auto) {
		EntityManager em = JpaUtil.getEntityManager();
		em.persist(auto);
		
		JpaUtil.evictCache(em, Automovel.LISTAR_DESTAQUES);
		
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
		
		TypedQuery<Automovel> query = JpaUtil.getEntityManager().createQuery(jpql, Automovel.class);
		for (Map.Entry<String, Object> param : params.entrySet()) {
			
			query.setParameter(param.getKey(), param.getValue());
		}
		
		automoveis = query.getResultList();
	}
	
	public List<Automovel> getAutomoveis() {
		if (automoveis == null) {
			automoveis = JpaUtil.getEntityManager().createNamedQuery(Automovel.LISTAR_DESTAQUES, Automovel.class).getResultList();
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
