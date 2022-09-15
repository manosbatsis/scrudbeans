package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.internal.artifacts.dependencies.ProjectDependencyInternal;
import org.gradle.api.internal.artifacts.DefaultProjectDependencyFactory;
import org.gradle.api.internal.artifacts.dsl.dependencies.ProjectFinder;
import org.gradle.api.internal.catalog.DelegatingProjectDependency;
import org.gradle.api.internal.catalog.TypeSafeProjectDependencyFactory;
import javax.inject.Inject;

@NonNullApi
public class RootProjectAccessor extends TypeSafeProjectDependencyFactory {


    @Inject
    public RootProjectAccessor(DefaultProjectDependencyFactory factory, ProjectFinder finder) {
        super(factory, finder);
    }

    /**
     * Creates a project dependency on the project at path ":"
     */
    public ScrudbeansProjectDependency getScrudbeans() { return new ScrudbeansProjectDependency(getFactory(), create(":")); }

    /**
     * Creates a project dependency on the project at path ":scrudbeans-annotation-processor-kotlin"
     */
    public ScrudbeansAnnotationProcessorKotlinProjectDependency getScrudbeansAnnotationProcessorKotlin() { return new ScrudbeansAnnotationProcessorKotlinProjectDependency(getFactory(), create(":scrudbeans-annotation-processor-kotlin")); }

    /**
     * Creates a project dependency on the project at path ":scrudbeans-api"
     */
    public ScrudbeansApiProjectDependency getScrudbeansApi() { return new ScrudbeansApiProjectDependency(getFactory(), create(":scrudbeans-api")); }

    /**
     * Creates a project dependency on the project at path ":scrudbeans-common"
     */
    public ScrudbeansCommonProjectDependency getScrudbeansCommon() { return new ScrudbeansCommonProjectDependency(getFactory(), create(":scrudbeans-common")); }

    /**
     * Creates a project dependency on the project at path ":scrudbeans-integration-tests-kotlin"
     */
    public ScrudbeansIntegrationTestsKotlinProjectDependency getScrudbeansIntegrationTestsKotlin() { return new ScrudbeansIntegrationTestsKotlinProjectDependency(getFactory(), create(":scrudbeans-integration-tests-kotlin")); }

    /**
     * Creates a project dependency on the project at path ":scrudbeans-jpa"
     */
    public ScrudbeansJpaProjectDependency getScrudbeansJpa() { return new ScrudbeansJpaProjectDependency(getFactory(), create(":scrudbeans-jpa")); }

    /**
     * Creates a project dependency on the project at path ":scrudbeans-spring-boot-autoconfigure"
     */
    public ScrudbeansSpringBootAutoconfigureProjectDependency getScrudbeansSpringBootAutoconfigure() { return new ScrudbeansSpringBootAutoconfigureProjectDependency(getFactory(), create(":scrudbeans-spring-boot-autoconfigure")); }

    /**
     * Creates a project dependency on the project at path ":scrudbeans-spring-boot-starter"
     */
    public ScrudbeansSpringBootStarterProjectDependency getScrudbeansSpringBootStarter() { return new ScrudbeansSpringBootStarterProjectDependency(getFactory(), create(":scrudbeans-spring-boot-starter")); }

}
