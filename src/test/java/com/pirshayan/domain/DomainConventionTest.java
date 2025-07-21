package com.pirshayan.domain;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import java.util.Set;

import com.tngtech.archunit.core.domain.JavaAnnotation;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

@AnalyzeClasses(packages = "com.pirshayan.domain", importOptions = { ImportOption.DoNotIncludeTests.class })
public class DomainConventionTest {
	// Rule 1: Aggregate Rules – All domain model classes that belong to an
	// aggregate
	// (except AggregateRoot classes, their ID classes, builders, and validators)
	// must be package-private.
	// This enforces encapsulation and ensures aggregate internals are not exposed
	// outside their package boundary.
	@ArchTest
	static ArchRule domain_models_except_ids_and_aggregateRoots_must_be_package_private = classes().that()
			.resideInAnyPackage("com.pirshayan.domain.model..").and().resideOutsideOfPackage("..exception..").and()
			.haveSimpleNameNotEndingWith("Id").and().haveSimpleNameNotEndingWith("AggregateRoot").and()
			.haveSimpleNameNotContaining("Builder").and().doNotHaveSimpleName("Validator").should().bePackagePrivate()
			.because("Entities and value objects inside an agrregate must be package private to enforce encapsulation");

	// Rule 2: Dependency Rule – All classes in the `domain.model` package,
	// except domain services, must depend only on other domain classes or on Java
	// SE libraries.
	// This ensures the domain model remains pure and independent of external
	// frameworks or layers.
	@ArchTest
	static ArchRule all_classes_in_domain_package_should_only_depend_on_domain_or_java_se = classes().that()
			.resideInAPackage("com.pirshayan.domain.model..").should().onlyDependOnClassesThat()
			.resideInAnyPackage("java..", "com.pirshayan.domain.model..")
			.because("Domain classes should only depend on Java SE or other domain classes");

	// Rule 3: Dependency Rule – Domain model classes that contain business logic
	// must not use annotations.
	// This ensures the domain layer remains framework-agnostic and focused purely
	// on business rules.
	@ArchTest
	static final ArchRule domain_model_classes_should_not_have_any_annotations = classes().that()
			.resideInAPackage("com.pirshayan.domain.model..").should(notBeAnnotated())
			.because("Domain model classes must be free of annotations to stay framework-free");

	// Rule 4: Dependency Rule – Domain service classes must only depend on other
	// domain classes.
	// This helps maintain the purity of the domain layer by avoiding dependencies
	// on external layers or frameworks.
	@ArchTest
	static ArchRule domain_service_classes_should_only_depend_on_domain_java_or_jakarta_ee_classes = classes().that()
			.resideInAPackage("com.pirshayan.domain.service..").should().onlyDependOnClassesThat()
			.resideInAnyPackage("com.pirshayan.domain..", "java..", "jakarta.enterprise.context..").because(
					"Domain service classes should on depend on Jakarta EE CDI specifications, Java SE libraries or other domain classes");

	// Rule 5: Domain service classes must be annotated with @ApplicationScoped.
	// This ensures they are properly managed by the CDI container while remaining
	// part of the domain layer.
	@ArchTest
	static ArchRule domain_service_classes_should_be_annotated_with_applicationScoped = classes().that()
			.resideInAPackage("com.pirshayan.domain.service..").and().haveSimpleNameEndingWith("DomainService").should()
			.beAnnotatedWith("jakarta.enterprise.context.ApplicationScoped")
			.because("Domain services should be application-scoped for proper lifecycle management");

	// Rule 6: Package Dependency Rule – Ensure there are no cyclic dependencies
	// between packages within the domain layer.
	// Cyclic dependencies reduce modularity, increase coupling, and make the code
	// harder to maintain and test.
	@ArchTest
	static ArchRule domain_packages_should_be_free_of_cycles = slices().matching("com.pirshayan.domain.(*)..").should()
			.beFreeOfCycles().because("Cyclic dependencies harm modularity and testability.");

	// Custom condition to check for absence of any annotations
	private static ArchCondition<JavaClass> notBeAnnotated() {
		return new ArchCondition<>("not be annotated") {
			@Override
			public void check(JavaClass clazz, ConditionEvents events) {
				Set<JavaAnnotation<JavaClass>> annotations = clazz.getAnnotations();
				if (!annotations.isEmpty()) {
					String message = String.format("Class %s is annotated with: %s", clazz.getName(),
							annotations.stream().map(a -> a.getRawType().getFullName()).toList());
					events.add(SimpleConditionEvent.violated(clazz, message));
				}
			}
		};
	}

}
