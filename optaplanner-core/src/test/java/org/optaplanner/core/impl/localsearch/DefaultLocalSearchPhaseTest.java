/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.localsearch;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchType;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.immovable.TestdataImmovableEntity;
import org.optaplanner.core.impl.testdata.domain.immovable.TestdataImmovableSolution;
import org.optaplanner.core.impl.testdata.domain.reinitialize.TestdataReinitializeEntity;
import org.optaplanner.core.impl.testdata.domain.reinitialize.TestdataReinitializeSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class DefaultLocalSearchPhaseTest {

    @Test
    public void solveWithInitializedEntities() {
        SolverFactory<TestdataSolution> solverFactory = PlannerTestUtils.buildSolverFactory(
                TestdataSolution.class, TestdataEntity.class);
        LocalSearchPhaseConfig phaseConfig = new LocalSearchPhaseConfig();
        phaseConfig.setTerminationConfig(new TerminationConfig().withScoreCalculationCountLimit(10L));
        solverFactory.getSolverConfig().setPhaseConfigList(Collections.singletonList(
                phaseConfig));
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        TestdataSolution solution = new TestdataSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Arrays.asList(
                new TestdataEntity("e1", v1),
                new TestdataEntity("e2", v2),
                new TestdataEntity("e3", v1)));

        solution = solver.solve(solution);
        assertNotNull(solution);
        TestdataEntity solvedE1 = solution.getEntityList().get(0);
        assertCode("e1", solvedE1);
        assertNotNull(solvedE1.getValue());
        TestdataEntity solvedE2 = solution.getEntityList().get(1);
        assertCode("e2", solvedE2);
        assertNotNull(solvedE2.getValue());
        TestdataEntity solvedE3 = solution.getEntityList().get(2);
        assertCode("e3", solvedE3);
        assertNotNull(solvedE3.getValue());
    }

    @Test
    public void solveWithImmovableEntities() {
        SolverFactory<TestdataImmovableSolution> solverFactory = PlannerTestUtils.buildSolverFactory(
                TestdataImmovableSolution.class, TestdataImmovableEntity.class);
        LocalSearchPhaseConfig phaseConfig = new LocalSearchPhaseConfig();
        phaseConfig.setTerminationConfig(new TerminationConfig().withScoreCalculationCountLimit(10L));
        solverFactory.getSolverConfig().setPhaseConfigList(Collections.singletonList(
                phaseConfig));
        Solver<TestdataImmovableSolution> solver = solverFactory.buildSolver();

        TestdataImmovableSolution solution = new TestdataImmovableSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Arrays.asList(
                new TestdataImmovableEntity("e1", v1, false),
                new TestdataImmovableEntity("e2", v2, true),
                new TestdataImmovableEntity("e3", null, true)));

        solution = solver.solve(solution);
        assertNotNull(solution);
        TestdataImmovableEntity solvedE1 = solution.getEntityList().get(0);
        assertCode("e1", solvedE1);
        assertNotNull(solvedE1.getValue());
        TestdataImmovableEntity solvedE2 = solution.getEntityList().get(1);
        assertCode("e2", solvedE2);
        assertEquals(v2, solvedE2.getValue());
        TestdataImmovableEntity solvedE3 = solution.getEntityList().get(2);
        assertCode("e3", solvedE3);
        assertEquals(null, solvedE3.getValue());
    }

    @Test
    public void solveWithEmptyEntityList() {
        SolverFactory<TestdataSolution> solverFactory = PlannerTestUtils.buildSolverFactory(
                TestdataSolution.class, TestdataEntity.class);
        LocalSearchPhaseConfig phaseConfig = new LocalSearchPhaseConfig();
        phaseConfig.setTerminationConfig(new TerminationConfig().withScoreCalculationCountLimit(10L));
        solverFactory.getSolverConfig().setPhaseConfigList(Collections.singletonList(
                phaseConfig));
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        TestdataSolution solution = new TestdataSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Collections.emptyList());

        solution = solver.solve(solution);
        assertNotNull(solution);
        assertEquals(0, solution.getEntityList().size());
    }

    @Test
    public void solveTabuSearchWithInitializedEntities() {
        SolverFactory<TestdataSolution> solverFactory = PlannerTestUtils.buildSolverFactory(
                TestdataSolution.class, TestdataEntity.class);
        LocalSearchPhaseConfig phaseConfig = new LocalSearchPhaseConfig();
        phaseConfig.setLocalSearchType(LocalSearchType.TABU_SEARCH);
        phaseConfig.setTerminationConfig(new TerminationConfig().withScoreCalculationCountLimit(10L));
        solverFactory.getSolverConfig().setPhaseConfigList(Collections.singletonList(
                phaseConfig));
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        TestdataSolution solution = new TestdataSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Arrays.asList(
                new TestdataEntity("e1", v1),
                new TestdataEntity("e2", v2),
                new TestdataEntity("e3", v1)));

        solution = solver.solve(solution);
        assertNotNull(solution);
        TestdataEntity solvedE1 = solution.getEntityList().get(0);
        assertCode("e1", solvedE1);
        assertNotNull(solvedE1.getValue());
        TestdataEntity solvedE2 = solution.getEntityList().get(1);
        assertCode("e2", solvedE2);
        assertNotNull(solvedE2.getValue());
        TestdataEntity solvedE3 = solution.getEntityList().get(2);
        assertCode("e3", solvedE3);
        assertNotNull(solvedE3.getValue());
    }

    @Test
    public void solveTabuSearchWithImmovableEntities() {
        SolverFactory<TestdataImmovableSolution> solverFactory = PlannerTestUtils.buildSolverFactory(
                TestdataImmovableSolution.class, TestdataImmovableEntity.class);
        LocalSearchPhaseConfig phaseConfig = new LocalSearchPhaseConfig();
        phaseConfig.setLocalSearchType(LocalSearchType.TABU_SEARCH);
        phaseConfig.setTerminationConfig(new TerminationConfig().withScoreCalculationCountLimit(10L));
        solverFactory.getSolverConfig().setPhaseConfigList(Collections.singletonList(
                phaseConfig));
        Solver<TestdataImmovableSolution> solver = solverFactory.buildSolver();

        TestdataImmovableSolution solution = new TestdataImmovableSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Arrays.asList(
                new TestdataImmovableEntity("e1", v1, false),
                new TestdataImmovableEntity("e2", v2, true),
                new TestdataImmovableEntity("e3", null, true)));

        solution = solver.solve(solution);
        assertNotNull(solution);
        TestdataImmovableEntity solvedE1 = solution.getEntityList().get(0);
        assertCode("e1", solvedE1);
        assertNotNull(solvedE1.getValue());
        TestdataImmovableEntity solvedE2 = solution.getEntityList().get(1);
        assertCode("e2", solvedE2);
        assertEquals(v2, solvedE2.getValue());
        TestdataImmovableEntity solvedE3 = solution.getEntityList().get(2);
        assertCode("e3", solvedE3);
        assertEquals(null, solvedE3.getValue());
    }

    @Test
    public void solveTabuSearchWithEmptyEntityList() {
        SolverFactory<TestdataSolution> solverFactory = PlannerTestUtils.buildSolverFactory(
                TestdataSolution.class, TestdataEntity.class);
        LocalSearchPhaseConfig phaseConfig = new LocalSearchPhaseConfig();
        phaseConfig.setLocalSearchType(LocalSearchType.TABU_SEARCH);
        phaseConfig.setTerminationConfig(new TerminationConfig().withScoreCalculationCountLimit(10L));
        solverFactory.getSolverConfig().setPhaseConfigList(Collections.singletonList(
                phaseConfig));
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        TestdataSolution solution = new TestdataSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Collections.emptyList());

        solution = solver.solve(solution);
        assertNotNull(solution);
        assertEquals(0, solution.getEntityList().size());
    }


}
