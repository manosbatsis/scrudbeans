package com.github.manosbatsis.scrudbeans.api.mdd.model;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

import com.github.manosbatsis.scrudbeans.api.mdd.ScrudModelProcessorException;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudResource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * A metadata and utility context helper focusing on a single model annotated with
 * {@link ScrudResource}. Used during javapoet-driven sourcecode generation.
 */
@Slf4j
@Data
public class ScrudModelDescriptor extends ModelDescriptor {

	private final ScrudResource scrudResource;

	private Map<String, String> oneToMany = new HashMap<>();

	private Map<String, String> oneToOne = new HashMap<>();

	private Map<String, String> manyToOne = new HashMap<>();

	private Map<String, String> manyToMany = new HashMap<>();

	public ScrudModelDescriptor(ProcessingEnvironment processingEnv, TypeElement typeElement) throws ScrudModelProcessorException {
		super(processingEnv, typeElement);
		this.scrudResource = typeElement.getAnnotation(ScrudResource.class);
	}

	@Override
	protected void scanMember(Types types, TypeElement currentTypeElement, Element memberElement) throws ScrudModelProcessorException {
		checkIfMemberIsId(types, memberElement);
	}
}
