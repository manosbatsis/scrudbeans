package com.github.manosbatsis.scrudbeans.processor.java.descriptor;

import com.github.manosbatsis.scrudbeans.api.mdd.ScrudModelProcessorException;
import lombok.NonNull;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Types;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.HashMap;
import java.util.Map;

/**
 * Base implementation for classes describing (entity) models
 */
public abstract class ModelDescriptor {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ModelDescriptor.class);

    public static final String STACK_JPA = "jpa";

    protected TypeElement typeElement;

	protected final Boolean jpaEntity;

	protected String idType;

	protected String idName;

	protected final String qualifiedName;

	protected final String simpleName;

	protected String packageName;

	protected String parentPackageName;

	protected Map<String, String> genericParamTypes = new HashMap<>();

	public ModelDescriptor(ProcessingEnvironment processingEnv, TypeElement typeElement) throws ScrudModelProcessorException {
		this.typeElement = typeElement;
		this.jpaEntity = typeElement.getAnnotation(Entity.class) != null;
		this.simpleName = this.typeElement.getSimpleName().toString();
		this.qualifiedName = this.typeElement.getQualifiedName().toString();
		this.packageName = this.qualifiedName.substring(0, this.qualifiedName.length() - (this.simpleName.length() + 1));
		this.parentPackageName = this.packageName.substring(0, this.packageName.lastIndexOf("."));
		Types types = processingEnv.getTypeUtils();
		scanMembers(types, typeElement);
	}

	abstract void scanMember(Types types, TypeElement currentTypeElement, Element memberElement) throws ScrudModelProcessorException;

	protected void scanMembers(Types types, TypeElement currentTypeElement) throws ScrudModelProcessorException {
		for (Element e : currentTypeElement.getEnclosedElements()) {
			this.scanMember(types, currentTypeElement, e);
		}
		currentTypeElement = asTypeElement(types, currentTypeElement.getSuperclass());
		if (!currentTypeElement.getQualifiedName().contentEquals(Object.class.getCanonicalName())) {
			scanMembers(types, currentTypeElement);
		}
	}

	protected void checkIfMemberIsId(Types types, Element e) throws ScrudModelProcessorException {
		if (e.getAnnotation(Id.class) != null || e.getAnnotation(EmbeddedId.class) != null) {
            // Only support singular ID types
            if (idType != null) {
                throw new ScrudModelProcessorException("Only singular IDs are supported");
            }
            idType = getMemberType(types, e);
            idName = e.getSimpleName().toString();
        }
	}

	/**
	 * Convert the given {@link TypeMirror} to a {@link TypeElement}
	 * @param typeMirror
	 * @return
	 * @throws ScrudModelProcessorException
	 */
	protected TypeElement asTypeElement(Types types, @NonNull TypeMirror typeMirror) throws ScrudModelProcessorException {
		if (typeMirror.getKind() != TypeKind.DECLARED) {
			throw new ScrudModelProcessorException();
		}
		Element element = ((DeclaredType) typeMirror).asElement();
		if (!(element.getKind().isClass() || element.getKind().isInterface())) {
			throw new ScrudModelProcessorException();
		}
		return (TypeElement) element;
	}

	/**
	 * Inspect member and get type if field or  getter, null otherwise.
	 * @param types
	 * @param scrudModelMember
	 * @return
	 * @throws ScrudModelProcessorException
	 */
	protected String getMemberType(Types types, Element scrudModelMember) throws ScrudModelProcessorException {
		String memberType = null;
		TypeMirror typeMirror = null;
		// If member is a field
		if (scrudModelMember.getKind() == ElementKind.FIELD) {
			VariableElement ve = (VariableElement) scrudModelMember;
			typeMirror = ve.asType();
			// replace generic type variables with the current concrete type
			if (typeMirror instanceof TypeVariable) {
				typeMirror = types.asMemberOf((DeclaredType) this.typeElement.asType(), ve);
			}
		}
		// If member is a getter
		else if (scrudModelMember.getKind() == ElementKind.METHOD && scrudModelMember.getSimpleName().toString().startsWith("get")) {
			ExecutableElement ee = (ExecutableElement) scrudModelMember;
			typeMirror = ee.getReturnType();
			// replace generic type variables with the current concrete type
			if (typeMirror instanceof TypeVariable) {
				typeMirror = types.asMemberOf((DeclaredType) this.typeElement.asType(), ee);
			}

		}

		// Get class/type name
		if (typeMirror != null) {
			memberType = asTypeElement(types, typeMirror).toString();
		}

		log.debug("getType for {}: {}", scrudModelMember.getSimpleName(), memberType);
		return memberType;
	}

	public String getStack() {
		return this.getJpaEntity() ? STACK_JPA : "";
    }

    public Boolean getJpaEntity() {
        return jpaEntity;
    }

    public String getIdType() {
        return idType;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getSimpleName() {
		return simpleName;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getParentPackageName() {
		return parentPackageName;
	}

	public Map<String, String> getGenericParamTypes() {
		return genericParamTypes;
	}

	public void setGenericParamTypes(Map<String, String> genericParamTypes) {
		this.genericParamTypes = genericParamTypes;
	}
}