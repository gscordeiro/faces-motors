package dev.gscordeiro.facesmotors.persistence;

import java.util.List;
import java.util.Map;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

public class QueryDataModel<T> extends LazyDataModel<T> {

	private static final long serialVersionUID = 5927740943028183630L;

	private String jpql;

	public QueryDataModel(String jpql) {
		this.jpql = jpql;

		String fromClause = jpql.substring(jpql.toLowerCase().indexOf("from"));
		Long count = JpaUtil.getEntityManager()
				.createQuery("select count(*) " + fromClause, Long.class)
				.getSingleResult();
		setRowCount(count.intValue());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {

		return JpaUtil.getEntityManager().createQuery(jpql)
								.setFirstResult(first)
								.setMaxResults(pageSize)
								.getResultList();
	}

}
