package br.com.casadocodigo.jsfjpa.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.hibernate.Session;

public class JpaUtil {

	private static final String PERSISTENCE_UNIT_NAME = "default";

	private static ThreadLocal<EntityManager> manager = new ThreadLocal<EntityManager>();

	private static EntityManagerFactory factory;

	private JpaUtil() {
	}

	public static EntityManager getEntityManager() {
		if (JpaUtil.factory == null) {
			JpaUtil.factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		}
		EntityManager m = JpaUtil.manager.get();
		if (m == null) {
			m = JpaUtil.factory.createEntityManager();
			JpaUtil.manager.set(m);
		}
		return m;
	}
	
	public static void evictCache(EntityManager em, String region){
		((Session)em.getDelegate()).getSessionFactory().getCache().evictQueryRegion(region);
	}

	public static void closeEntityManager() {
		EntityManager m = JpaUtil.manager.get();
		if (m != null) {
			EntityTransaction t = m.getTransaction();
			if (t.isActive()) { // TODO
			// CoreLog.getInstance()
			// .getLog()
			// .warn("EntityManager contains an active transaction, commiting transaction");
			// t.commit();
			}
			m.flush();
			m.close();
			JpaUtil.manager.set(null);
		}
	}
	
	public static void closeEntityManagerFactory(){
		closeEntityManager();
		JpaUtil.factory.close();
	}
}
