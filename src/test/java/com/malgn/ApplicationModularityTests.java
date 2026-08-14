package com.malgn;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;

import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

import org.junit.jupiter.api.Test;

class ApplicationModularityTests {

    ApplicationModules modules = ApplicationModules.of(Application.class, resideInAnyPackage("com.malgn.starter.."));

    @Test
    void verifyModularStructure() {
        modules.verify();
    }

    @Test
    void printModuleOverview() {
        modules.forEach(System.out::println);
    }

    @Test
    void generateDocumentation() {
        ApplicationModules modules = ApplicationModules.of(Application.class);

        new Documenter(modules)
            .writeModulesAsPlantUml()           // PlantUML 다이어그램
            .writeIndividualModulesAsPlantUml() // 모듈별 상세
            .writeModuleCanvases();             // 모듈 캔버스
    }
}
