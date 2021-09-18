package com.crosskey.mortagecalculator;


import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.text.DecimalFormat;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;


class MortageCalculatorApplicationTests{

    private static DecimalFormat df = new DecimalFormat("0.00");


    @Test
    public void testCalculatePower() {
        MortageCalculatorController mortagePlan = new MortageCalculatorController();
        assertEquals(1.0, mortagePlan.calculatePower(1,1));
        assertEquals(1.0, mortagePlan.calculatePower(5,0));
        assertEquals(0.0, mortagePlan.calculatePower(0,1));
        assertEquals(4.0, mortagePlan.calculatePower(2,2));
        assertEquals(1.0, mortagePlan.calculatePower(-1,4));
        assertEquals(-1.0, mortagePlan.calculatePower(-1,3));

    }

    @Test
    public void testCalculateMortage() throws SQLException {
        MortageCalculatorController mortagePlan = new MortageCalculatorController();
        Prospect prospect1 = new Prospect();
        prospect1.setName("Max");
        prospect1.setInterest(10.0);
        prospect1.setTotalLoan(1000.0);
        prospect1.setYears(10);
        double mortage = mortagePlan.calculateMortage(prospect1);
        assertNotNull(mortage);
        // DecimalFormat returns a string so that's why we are comparing Strings
        // expected result done by calculator
        assertEquals( "13.22", df.format(mortage));

        Prospect prospect2 = new Prospect();
        prospect2.setName("Tom");
        prospect2.setInterest(0.0);
        prospect2.setTotalLoan(1000.0);
        prospect2.setYears(10);
        mortage = mortagePlan.calculateMortage(prospect2);
        // because of interest being 0 it leads to [(1+b)^p -1] being 0.0 and can't divide by that
        boolean isNan = Double.isNaN(mortage);
        assertTrue(isNan);

    }

}
