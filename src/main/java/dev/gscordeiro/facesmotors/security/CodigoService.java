package dev.gscordeiro.facesmotors.security;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

/**
 * Cifra/decifra a chave primária (Long) de uma entidade em uma String
 * URL-safe, para que possamos expor um identificador navegável e
 * bookmarkable sem revelar o valor (e o tamanho/sequência) do PK.
 *
 * <p>Usamos AES com uma chave fixa de 16 bytes carregada como
 * {@link SecretKeySpec}. O bloco AES tem 16 bytes, então o {@code Long}
 * de 8 bytes é cifrado em um único bloco e o resultado é codificado em
 * Base64 URL-safe (sem padding), gerando um código de 22 caracteres.</p>
 *
 * <p>O bean é registrado em {@code @Named("codigos")} para ser usado
 * diretamente do EL nas páginas Facelets, por exemplo:
 * {@code #{codigos.encriptar(automovel.id)}}.</p>
 *
 * <p><b>Nota didática:</b> a chave está fixa apenas para simplificar o
 * exemplo do livro. Em produção, leia de uma propriedade externa
 * (variável de ambiente, MicroProfile Config, secret manager, etc.).</p>
 */
@Named("codigos")
@ApplicationScoped
public class CodigoService {

	private static final String ALGORITMO = "AES";
	private static final String TRANSFORMACAO = "AES/ECB/PKCS5Padding";
	private static final SecretKeySpec CHAVE = new SecretKeySpec(
			"FacesMotors2026!".getBytes(StandardCharsets.UTF_8), ALGORITMO);

	public String encriptar(Long id) {
		if (id == null) return null;
		try {
			Cipher cipher = Cipher.getInstance(TRANSFORMACAO);
			cipher.init(Cipher.ENCRYPT_MODE, CHAVE);
			byte[] plainText = ByteBuffer.allocate(Long.BYTES).putLong(id).array();
			byte[] cipherText = cipher.doFinal(plainText);
			return Base64.getUrlEncoder().withoutPadding().encodeToString(cipherText);
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("Falha ao criptografar id " + id, e);
		}
	}

	public Long decriptar(String codigo) {
		if (codigo == null || codigo.isBlank()) return null;
		try {
			Cipher cipher = Cipher.getInstance(TRANSFORMACAO);
			cipher.init(Cipher.DECRYPT_MODE, CHAVE);
			byte[] cipherText = Base64.getUrlDecoder().decode(codigo);
			byte[] plainText = cipher.doFinal(cipherText);
			return ByteBuffer.wrap(plainText).getLong();
		} catch (IllegalArgumentException | GeneralSecurityException e) {
			return null;
		}
	}
}
