package com.example.quantify;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExperimentTest {
    private ArrayList<Experiment> mockExperimentList(){
        ArrayList<Experiment> expList = new ArrayList<>();
        expList.add(mockExperiment());
        return expList;
    }

    private Experiment mockExperiment(){
        return new Experiment("Exp1", "Nahin", "Running", "Binomial");
    }
    @Test
    public void testAdd() {
        ArrayList<Experiment> expList = mockExperimentList();

        assertEquals(1, expList.size());

        Experiment exp = new Experiment("Exp2", "Nahin1", "Running", "Binomial");
        expList.add(exp);

        assertEquals(2, expList.size());
        assertTrue(expList.contains(exp));
    }

    @org.junit.jupiter.api.Test
    void testGetExperiment() {
        ArrayList<Experiment> expList = mockExperimentList();
        Experiment exp = new Experiment("Exp2", "Nahin1", "Running", "Binomial");
        expList.add(mockExperiment());
        Assertions.assertEquals(0, mockExperiment().compareTo(expList.get(0)));
        Assertions.assertEquals(1, exp.compareTo(expList.get(1)));


    }
}
