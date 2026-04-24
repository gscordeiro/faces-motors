package dev.gscordeiro.facesmotors.controllers;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import dev.gscordeiro.facesmotors.entities.Automovel;
import dev.gscordeiro.facesmotors.entities.Marca;
import dev.gscordeiro.facesmotors.entities.Modelo;
import dev.gscordeiro.facesmotors.security.CodigoService;
import dev.gscordeiro.facesmotors.services.AutomovelService;

/**
 * Backing bean único para todas as telas que falam sobre Automóvel:
 * cadastro/edição, listagem administrativa, vitrine pública e tela de
 * visualização stateless.
 *
 * Aqui só ficam responsabilidades de tela (estado, navegação e mensagens).
 * Toda a interação com o banco passa pelo {@link AutomovelService}, que por
 * sua vez delega para o repositório Jakarta Data. O serviço precisa estar no
 * meio porque o repositório gerado é {@code @Dependent} e mantém uma
 * {@code StatelessSession} não-serializável, o que conflita com este bean
 * {@code @ViewScoped} (escopo passivável).
 */
@Named
@ViewScoped
public class AutomovelBean implements Serializable {

	private static final long serialVersionUID = -8780407253943723401L;

	@Inject
	private AutomovelService servico;

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

	public String salvar(Automovel auto) {
		servico.salvar(auto);
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage("Automóvel salvo com sucesso!"));
		return "listar?faces-redirect=true";
	}

	// ======================================================================
	// LISTAGEM (admin) e VITRINE pública
	// ======================================================================

	public List<Automovel> getAutomoveis() {
		if (automoveis == null) {
			automoveis = servico.listar();
		}
		return automoveis;
	}

	public int getTotalAutomoveis() {
		return getAutomoveis().size();
	}

	public void buscarAutomoveis(Modelo modelo) {
		automoveis = servico.buscar(modelo);
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

		Optional<Automovel> encontrado;
		try {
			encontrado = servico.buscarComRelacoes(id);
		} catch (Exception e) {
			adicionarAviso("Erro na base de dados ao recuperar o veículo com múltiplas fotos.");
			return;
		}

		if (encontrado.isPresent()) {
			automovel = encontrado.get();
		} else {
			adicionarAviso("Veículo não encontrado ou já vendido.");
		}
	}

	public List<Automovel> getSugestoes() {
		if (sugestoes != null) return sugestoes;
		sugestoes = isEncontrado() ? servico.sugestoesPara(automovel) : List.of();
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

	public Automovel getAutomovel() {
		return automovel;
	}

	public void setAutomovel(Automovel automovel) {
		this.automovel = automovel;
	}

	public Marca getMarca() {
		return marca;
	}

	public void setMarca(Marca marca) {
		this.marca = marca;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
}
