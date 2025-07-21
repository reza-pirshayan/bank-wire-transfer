package com.pirshayan.application;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.pirshayan.application", importOptions = { ImportOption.DoNotIncludeTests.class })
public class ApplicationConventionTest {
	@ArchTest
	static ArchRule application_layer_must_only_depend_on_domain_layer = classes().that()
			.resideInAnyPackage("com.pirshayan.application..").should().onlyDependOnClassesThat()
			.resideInAnyPackage("com.pirshayan.application..","com.pirshayan.domain..","java..", "jakarta..")
			.because("he dependency rule must be enforced.");
}
