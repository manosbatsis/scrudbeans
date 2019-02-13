package com.github.manosbatsis.scrudbeans.api.mdd.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.util.Types;

import com.github.manosbatsis.scrudbeans.api.mdd.ScrudModelProcessorException;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * A metadata and utility context helper focusing on a single model annotated with
 * {@link ScrudBean}. Used during javapoet-driven sourcecode generation.
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class ScrudModelDescriptor extends ModelDescriptor {


	private final ScrudBean scrudBean;

	private Map<String, String> oneToMany = new HashMap<>();

	private Map<String, String> oneToOne = new HashMap<>();

	private Map<String, String> manyToOne = new HashMap<>();

	private Map<String, String> manyToMany = new HashMap<>();

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
		dtoTypes = toAnnotationClassNamesValueStream(typeElement, ScrudBean.class, "dtoTypes")
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
	 * @param annotationClass
	 * @param annotationAttributeName
	 * @return a stream with the value classnames
	 */
	private Stream<String> toAnnotationClassNamesValueStream(TypeElement typeElement, Class annotationClass, String annotationAttributeName) {
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

	@Override
	protected void scanMember(Types types, TypeElement currentTypeElement, Element memberElement) throws ScrudModelProcessorException {
		checkIfMemberIsId(types, memberElement);
	}

}
