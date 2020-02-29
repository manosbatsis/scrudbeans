package com.github.manosbatsis.scrudbeans.processor.java;

import com.github.manosbatsis.scrudbeans.api.DtoMapper;
import com.github.manosbatsis.scrudbeans.api.mdd.ScrudModelProcessorException;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean;
import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter;
import com.github.manosbatsis.scrudbeans.processor.java.descriptor.EntityModelDescriptor;
import com.github.manosbatsis.scrudbeans.processor.java.descriptor.ModelDescriptor;
import com.github.manosbatsis.scrudbeans.processor.java.descriptor.ScrudModelDescriptor;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.persistence.Entity;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.*;

/**
 * Annotation processor that generates SCRUD components
 * for model annotated with @{@link ScrudBean}
 * and JPA specification predicate factories for models
 * annotated with @{@link Entity}
 */
@SupportedAnnotationTypes({
        "com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ScrudModelAnnotationProcessor extends AbstractProcessor {

	private static final Logger log = LoggerFactory.getLogger(ScrudModelAnnotationProcessor.class);

	private boolean complete = false;

	private Filer filer;

	// Config properties, i.e. "application.properties" from the classpath
	private Properties configProps;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		// short-circuit if there are multiple rounds
		if (complete) {
			log.info("Processor has already been executed, ignoring");
			return true;
		}
		log.info("ScrudModelAnnotationProcessor processing started");
		// Init a filer
		this.filer = processingEnv.getFiler();
		// Load config/properties
		configProps = this.loadProperties();
		// Create JPA query predicate factories for each entity in the source path
		generateEntityPredicateFactories(roundEnv);
		// Create other SCRUD components for each model annotated with ScrudBean
		generateScrudComponents(roundEnv);
		// Claiming that annotations have been processed by this processor
		complete = true;
		return true;
	}

	/**
	 * Create SCRUD components for the target model
	 * @param roundEnv The current compilation round environment
	 */
	private void generateScrudComponents(RoundEnvironment roundEnv) {
		Set<? extends Element> annotatedModels = roundEnv.getElementsAnnotatedWith(ScrudBean.class);
		Map<Name, ScrudModelDescriptor> modelDescriptors = new HashMap<>();
		log.info("ScrudModelAnnotationProcessor found {} annotated classes", (annotatedModels != null ? annotatedModels.size() : 0));
		if (annotatedModels != null) {
			for (final Element element : annotatedModels) {
				try {
					if (element instanceof TypeElement) {
                        final TypeElement typeElement = (TypeElement) element;
                        // Parse model to something more convenient
                        ScrudModelDescriptor descriptor = new ScrudModelDescriptor(processingEnv, typeElement, configProps);
                        // Generate components for model
                        generateDtoMappers(descriptor);
                        createIdAdapter(descriptor);
                        createRepository(descriptor);
                        createService(descriptor);
                        createController(descriptor);
                    }
					else {
						log.warn("Not an instance of TypeElement but annotated with ScrudBean: {}", element.getSimpleName());
					}
				}
				catch (ScrudModelProcessorException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Create JPA query predicate factories for each entity in the source path
	 * @param roundEnv The current compilation round environment
	 */
	private void generateEntityPredicateFactories(RoundEnvironment roundEnv) {
		Set<? extends Element> entities = roundEnv.getElementsAnnotatedWith(ScrudBean.class);
		for (final Element element : entities) {
			try {
				if (element.getAnnotation(Entity.class) != null) {
					if (element instanceof TypeElement) {
						log.debug("generateEntityPredicateFactories, processing element: {}", element.getSimpleName());
						final TypeElement typeElement = (TypeElement) element;
						EntityModelDescriptor descriptor = new EntityModelDescriptor(processingEnv, typeElement);
						createPredicateFactory(descriptor);
					}
					else {
						log.warn("Not an instance of TypeElement but annotated with ScrudBean: {}", element.getSimpleName());
                    }
                }
            } catch (RuntimeException | ScrudModelProcessorException e) {
                log.error("Error generating components for {}: " + e.getMessage(),
                        element.getSimpleName(), e);
            }
        }
    }

    /**
     * Create an {@link IdentifierAdapter} implementation
     *
     * @param descriptor The target model descriptor
     * @return the written file
     */
    private JavaFile createIdAdapter(ScrudModelDescriptor descriptor) {
        TypeSpec typeSpec = TypeSpecBuilder.createIdAccessor(descriptor);
        return writeJavaFile(descriptor, typeSpec, descriptor.getPackageName());
    }

    /**
     * Create a SCRUD REST controller source file
     *
     * @param descriptor The target model descriptor
     * @return the written file
     */
    private JavaFile createController(ScrudModelDescriptor descriptor) {
        // Skip controller generation if controllerSuperClass is set to NONE
        if (!ScrudBean.NONE.equals(descriptor.getScrudBean().controllerSuperClass())) {
            TypeSpec typeSpec = TypeSpecBuilder.createController(descriptor);
			return writeJavaFile(descriptor, typeSpec, descriptor.getParentPackageName() + ".controller");
		}
		return null;
	}

	/**
	 * Create {@link DtoMapper}s for the ScudBeans' target DTOs
	 * @param descriptor The target model descriptor
	 * @return the mapper files
	 */
	private List<JavaFile> generateDtoMappers(ScrudModelDescriptor descriptor) {
		List<JavaFile> files = new LinkedList<>();
		descriptor.getDtoTypes().forEach((dtoClass) -> {
			TypeSpec typeSpec = TypeSpecBuilder.createDtoMapper(descriptor, dtoClass);
			files.add(writeJavaFile(
					descriptor,
					typeSpec,
					descriptor.getParentPackageName() + ".mapper"));
		});
		return files;
	}
	/**
	 * Create SCRUD service source files
	 * @param descriptor The target model descriptor
	 * @return the written files: interface and implementation
	 */
	private List<JavaFile> createService(ScrudModelDescriptor descriptor) {
		List<JavaFile> files = new LinkedList<>();
		// Ensure a service has not already been created
		String serviceQualifiedName = descriptor.getParentPackageName() +
				".service." + descriptor.getSimpleName() + "Service";
		TypeElement existing = processingEnv.getElementUtils().getTypeElement(serviceQualifiedName);
		if (Objects.isNull(existing)) {
			files.add(createServiceInterface(descriptor));
			files.add(createServiceImpl(descriptor));
		}
		else {
			log.debug("createService: {} already exists, skipping", serviceQualifiedName);
		}
		return files;
	}

	/**
	 * Create a SCRUD service interface source file
	 * @param descriptor The target model descriptor
	 * @return the written file
	 */
	private JavaFile createServiceInterface(ScrudModelDescriptor descriptor) {
		TypeSpec typeSpec = TypeSpecBuilder.createServiceInterface(descriptor);
		return writeJavaFile(descriptor, typeSpec, descriptor.getParentPackageName() + ".service");

	}

	/**
	 * Create a SCRUD service implementation source file
	 * @param descriptor The target model descriptor
	 * @return the written file
	 */
	private JavaFile createServiceImpl(ScrudModelDescriptor descriptor) {
		TypeSpec typeSpec = TypeSpecBuilder.createServiceImpl(descriptor);
		return writeJavaFile(descriptor, typeSpec, descriptor.getParentPackageName() + ".service");
	}

	/**
	 * Create a SCRUD repository source file
	 * @param descriptor The target model descriptor
	 * @return the written file
	 */
	private JavaFile createRepository(ScrudModelDescriptor descriptor) {
		TypeSpec typeSpec = TypeSpecBuilder.createRepository(descriptor);
		return writeJavaFile(descriptor, typeSpec, descriptor.getParentPackageName() + ".repository");
	}

	/**
	 * Create a JPA specification predicate factory source file
	 * @param descriptor The target model descriptor
	 * @return the written file
	 */
	private JavaFile createPredicateFactory(EntityModelDescriptor descriptor) {
		TypeSpec typeSpec = TypeSpecBuilder.createPredicateFactory(descriptor);
		return writeJavaFile(descriptor, typeSpec, descriptor.getParentPackageName() + ".specification");
	}

	/**
	 * Write and return a source file for the given {@link TypeSpec}
	 * @param typeSpec The target model type spec
	 * @param descriptor The target model descriptor
	 * @param packageName The target source file package
	 * @return the written file
	 */
	private JavaFile writeJavaFile(ModelDescriptor descriptor, TypeSpec typeSpec, String packageName) {
		JavaFile file = null;
		String fileObjectName = packageName + "." + typeSpec.name;
		try {
			TypeElement existing = processingEnv.getElementUtils().getTypeElement(fileObjectName);
			if (existing == null) {
				log.debug("writeJavaFile for {}, filer: {}", fileObjectName, filer);
				file = JavaFile
						.builder(packageName, typeSpec)
						.build();
				file.writeTo(filer);
			}
			else {
				log.debug("writeJavaFile: Skipping for {} as it already exists", fileObjectName);
			}
		}
		catch (Exception e) {
			log.error("writeJavaFile: Error creating file for {}: " + e.getMessage(), fileObjectName, e);
		}
		return file;
	}

	private Properties loadProperties() {
		Properties props = null;
		try {
			FileObject fileObject = this.filer
					.getResource(StandardLocation.CLASS_OUTPUT, "", "application.properties");
			props = new Properties();
			props.load(fileObject.openInputStream());
			log.debug("loadProperties, props: {}", props);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}

}