package br.com.casadocodigo.jsfjpa.controllers;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.primefaces.model.LazyDataModel;

import br.com.casadocodigo.jsfjpa.entities.Modelo;
import br.com.casadocodigo.jsfjpa.persistence.JpaUtil;
import br.com.casadocodigo.jsfjpa.persistence.QueryDataModel;


@ManagedBean
@ViewScoped
public class ModeloBean implements Serializable{

	private static final long serialVersionUID = -8606041573319607244L;

	private Modelo modelo;
	private List<Modelo> modelos;

	private LazyDataModel<Modelo> lazyDataModel;

	@PostConstruct
	public void init(){
		modelo = new Modelo();
	}
	
	
	public String salvar(Modelo modelo) {
		EntityManager em = JpaUtil.getEntityManager();
		em.persist(modelo);
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Modelo salvo com sucesso!"));
		
		return "listar";
	}

	public LazyDataModel<Modelo> getLazyDataModel() {
		if (lazyDataModel == null) {
			String jpql = "select m from Modelo m";
			lazyDataModel = new QueryDataModel<Modelo>(jpql);
		}

		return lazyDataModel;
	}
	
	public List<Modelo> getModelos() {
		if (modelos == null) {
			modelos = JpaUtil.getEntityManager().createQuery("select m from Modelo m", Modelo.class).getResultList();
		}

		return modelos;
	}

	public Modelo getModelo() {
		return modelo;
	}

	public void setModelo(Modelo modelo) {
		this.modelo = modelo;
	}

	public void setModelos(List<Modelo> modelos) {
		this.modelos = modelos;
	}
	
}
