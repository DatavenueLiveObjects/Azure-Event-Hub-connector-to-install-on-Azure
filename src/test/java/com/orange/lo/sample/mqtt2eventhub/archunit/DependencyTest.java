package com.orange.lo.sample.mqtt2eventhub.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.library.DependencyRules;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(
        packages = "com.orange.lo.sample.mqtt2eventhub",
        importOptions = {
                ImportOption.DoNotIncludeTests.class,
                ImportOption.DoNotIncludeJars.class
        }
)
class DependencyTest {

    @ArchTest
    void shouldNotHaveCyclicalDependenciesBetweenClasses(JavaClasses classes) {
        slices().matching("com.orange.lo.sample.mqtt2eventhub.(**)")
                .should().beFreeOfCycles()
                .check(classes);
    }

    @ArchTest
    void noClassesShouldDependsOnUpperPackages(JavaClasses classes) {
        DependencyRules.NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES.check(classes);
    }

}