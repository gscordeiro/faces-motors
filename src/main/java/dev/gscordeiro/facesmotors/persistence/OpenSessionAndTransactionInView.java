package dev.gscordeiro.facesmotors.persistence;

import java.io.IOException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

@WebFilter("/*")
public class OpenSessionAndTransactionInView implements Filter{

	@Override
	public void destroy() {
		JpaUtil.closeEntityManagerFactory();
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		//inicia a transação antes de processar o request
		EntityManager em = JpaUtil.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
		
			//processa a requisição
			chain.doFilter(request, response);
			
			//faz commit
			tx.commit();
		} catch (Exception e) { //ou em caso de erro faz o rollback
			if(tx != null && tx.isActive()){
				tx.rollback();
			}
		}
		finally {
			
			 if(em.isOpen()){
				 em.close();
			 }
			
			 //ou simplesmente
			 //JpaUtil.closeEntityManager();
		}
		
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//não precisa fazer nada
		
	}
	
	
}
