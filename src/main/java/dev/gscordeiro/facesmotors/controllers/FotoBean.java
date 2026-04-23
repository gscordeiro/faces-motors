package dev.gscordeiro.facesmotors.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import dev.gscordeiro.facesmotors.entities.Automovel;

/**
 * Helper exposto ao EL para resolver a imagem que representa um automóvel.
 *
 * Enquanto não houver upload de fotos reais, mantém um banco de placeholders
 * SVG categorizados por tipo de carroceria. Quando passar a haver fotos reais,
 * basta sobrescrever {@link Foto#nome} no upload — a EL na vitrine e na tela de
 * visualização não muda.
 */
@Named("foto")
@ApplicationScoped
public class FotoBean {

	private static final String PLACEHOLDER_PADRAO = "placeholder-sedan.svg";

	public String caminho(Automovel auto) {
		if (auto == null) return PLACEHOLDER_PADRAO;
		if (auto.getFotos() == null || auto.getFotos().isEmpty()) return PLACEHOLDER_PADRAO;
		String nome = auto.getFotos().get(0).getNome();
		return (nome == null || nome.isBlank()) ? PLACEHOLDER_PADRAO : nome;
	}
}
