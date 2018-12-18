package com.github.manosbatsis.scrudbeans.api.mdd.model;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import javax.persistence.Entity;

import com.github.manosbatsis.scrudbeans.api.mdd.ScrudModelProcessorException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * A metadata and utility context helper focusing on a single model annotated with
 * {@link Entity}. Used during javapoet-driven sourcecode generation.
 */
@Slf4j
@Data
public class EntityModelDescriptor extends ModelDescriptor {

	public EntityModelDescriptor(ProcessingEnvironment processingEnv, TypeElement typeElement) throws ScrudModelProcessorException {
		super(processingEnv, typeElement);
	}

	@Override
	protected void scanMember(Types types, TypeElement currentTypeElement, Element memberElement) throws ScrudModelProcessorException {
		checkIfMemberIsId(types, memberElement);
	}

}
