package br.com.casadocodigo.jsfjpa.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import org.hibernate.Cache;
import org.hibernate.Session;

public class JpaUtil {

	private static final String PERSISTENCE_UNIT_NAME = "default";

	private static ThreadLocal<EntityManager> manager = new ThreadLocal<EntityManager>();

	private static EntityManagerFactory factory;

	private JpaUtil() {
	}

	public static boolean isEntityManagerOpen(){
		return JpaUtil.manager.get() != null && JpaUtil.manager.get().isOpen();
	}
	
	public static EntityManager getEntityManager() {
		if (JpaUtil.factory == null) {
			JpaUtil.factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		}
		EntityManager em = JpaUtil.manager.get();
		if (em == null || !em.isOpen()) {
			em = JpaUtil.factory.createEntityManager();
			JpaUtil.manager.set(em);
		}
		return em;
	}
	
	public static void evictCache(EntityManager em, String region){
		Cache cache = em.unwrap(Session.class).getSessionFactory().getCache();
		cache.evictQueryRegion(region);
	}

	public static void closeEntityManager() {
		EntityManager em = JpaUtil.manager.get();
		if (em != null) {
			EntityTransaction tx = em.getTransaction();
			if (tx.isActive()) { 
				tx.commit();
			}
			em.close();
			JpaUtil.manager.set(null);
		}
	}
	
	public static void closeEntityManagerFactory(){
		closeEntityManager();
		JpaUtil.factory.close();
	}
}
