package dev.gscordeiro.facesmotors.persistence;

import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

public class QueryDataModel<T> extends LazyDataModel<T> {

	private static final long serialVersionUID = 5927740943028183630L;

	private String jpql;
	private EntityManager em;

	public QueryDataModel(String jpql, EntityManager em) {
		this.jpql = jpql;
		this.em = em;
	}

	@Override
	public int count(Map<String, FilterMeta> filterBy) {
		String fromClause = jpql.substring(jpql.toLowerCase().indexOf("from"));
		Long count = em.createQuery("select count(*) " + fromClause, Long.class)
				.getSingleResult();
		return count.intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
		return em.createQuery(jpql)
				.setFirstResult(first)
				.setMaxResults(pageSize)
				.getResultList();
	}

}
