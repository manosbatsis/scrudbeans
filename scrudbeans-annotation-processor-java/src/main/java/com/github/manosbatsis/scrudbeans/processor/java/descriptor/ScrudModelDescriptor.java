package com.github.manosbatsis.scrudbeans.processor.java.descriptor;

import com.github.manosbatsis.scrudbeans.api.mdd.ScrudModelProcessorException;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.util.Types;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A metadata and utility context helper focusing on a single model annotated with
 * {@link ScrudBean}. Used during javapoet-driven sourcecode generation.
 */
public class ScrudModelDescriptor extends ModelDescriptor {

	private static final Logger log = LoggerFactory.getLogger(ScrudModelDescriptor.class);

	private final ScrudBean scrudBean;

	private Set<String> dtoTypes;

	private Properties configProperties;

	public ScrudModelDescriptor(ProcessingEnvironment processingEnv, TypeElement typeElement, Properties props) throws ScrudModelProcessorException {
		super(processingEnv, typeElement);
		this.configProperties = props;
		this.scrudBean = typeElement.getAnnotation(ScrudBean.class);
		initDtoClassnames(typeElement);

	}

	/** Initialise the set of DTO classnames for this ScrudBean. Used to create mappers from/to this ScrudBean */
	private void initDtoClassnames(TypeElement typeElement) {
		// Get DTO classnames from "dtoTypes"
		dtoTypes = annotationClassNamesValueStream(typeElement, ScrudBean.class, "dtoTypes")
				.filter(it -> !Object.class.getCanonicalName().equals(it))
				.collect(Collectors.toSet());
		// Add DTO classnames from "dtoTypeNames"
		this.scrudBean.dtoTypeNames();
		if (this.scrudBean.dtoTypeNames().length > 0) {
			for (String typeName : this.scrudBean.dtoTypeNames()) {
				if (StringUtils.isNotBlank(typeName)) this.dtoTypes.add(typeName);
			}
		}
	}

	/**
	 * Avoid {@link MirroredTypesException} when retrieving Class or Class[] typed annotation attribute values
	 * @param typeElement
	 * @param annotationAttributeName
	 * @return a stream with the value classnames
	 */
	private Stream<String> annotationClassNamesValueStream(TypeElement typeElement, Class annotationClass, String annotationAttributeName) {
		return typeElement.getAnnotationMirrors()
				.stream()
				.filter(annotationMirror -> annotationMirror.getAnnotationType().toString().contains(annotationClass.getName()))
				.map(AnnotationMirror::getElementValues)
				.flatMap(l -> l.entrySet().stream())
				.filter(entry -> entry.getKey().getSimpleName().contentEquals(annotationAttributeName))
				.flatMap(entry -> {
					Object value = entry.getValue().getValue();
					if (value instanceof List) return ((List<Object>) value).stream();
					else if (value.getClass().isArray()) return Arrays.stream((Object[]) value);
					else return Stream.of(value);
				})
				.map(it -> {
					String val = it.toString();
					if (val.endsWith(".class")) val = val.substring(0, val.length() - 6);
					return val;
				});
	}
	public Set<String> scrudBeanClassNamesValue(String annotationAttributeName){
		return annotationClassNamesValueStream(typeElement, ScrudBean.class, annotationAttributeName)
				.collect(Collectors.toSet());
	}

	@Override
	protected void scanMember(Types types, TypeElement currentTypeElement, Element memberElement) throws ScrudModelProcessorException {
		checkIfMemberIsId(types, memberElement);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ScrudModelDescriptor that = (ScrudModelDescriptor) o;
		return Objects.equals(scrudBean, that.scrudBean) &&
				Objects.equals(dtoTypes, that.dtoTypes) &&
				Objects.equals(configProperties, that.configProperties);
	}

	@Override
	public int hashCode() {
		return Objects.hash(scrudBean, dtoTypes, configProperties);
	}

	public ScrudBean getScrudBean() {
		return scrudBean;
	}

	public Set<String> getDtoTypes() {
		return dtoTypes;
	}

	public Properties getConfigProperties() {
		return configProperties;
	}
}