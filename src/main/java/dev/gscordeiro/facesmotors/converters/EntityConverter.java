package dev.gscordeiro.facesmotors.converters;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.gscordeiro.facesmotors.persistence.JpaUtil;


@FacesConverter(value = "entityConverter", managed = true)
public class EntityConverter implements Converter<Object> {

	private Logger logger = LoggerFactory.getLogger(EntityConverter.class);
	
	@Override
	public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
		if(object == null) return null;
		
		try {
			Class<?> classe = object.getClass();
			Long id = (Long) classe.getMethod("getId").invoke(object);
			
			return classe.getName() + "-" + id;
		} catch (Exception e) {
			logger.error("Erro ao converter entidade em String", e);
			return null;
		}
		
	}
	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent component, String string) {
		if(string == null || string.isEmpty()) return null;
		
		try {
			String[] values = string.split("-");
			return JpaUtil.getEntityManager().find(Class.forName(values[0]), Long.valueOf(values[1]));
		} catch (Exception e) {
			logger.error("Erro ao converter String em entidade", e);
			return null;
		}
	}


}
