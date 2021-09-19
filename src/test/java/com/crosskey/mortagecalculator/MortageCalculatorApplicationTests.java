package com.crosskey.mortagecalculator;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;


class MortageCalculatorApplicationTests{

    private static DecimalFormat df = new DecimalFormat("0.00");
    File file;


    @Rule
    private TemporaryFolder temporaryFolder = new TemporaryFolder();




    @Test// This is an integration test but decided I wanted to test it since I had some issues
    public void readFileTest() throws IOException {
        MortageCalculatorController mortagePlan = new MortageCalculatorController();
        File file = File.createTempFile( "prospectsTemp", ".txt");
        assertTrue(file.exists());
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String line1= "readFile skips first line so this needs to be here";
        String line2 = "Max, 234, 1, 4";
        String line3= "Tom, 214, 3, 5";
        writer.write(line1);
        writer.write("\n" + line2);
        writer.write("\n" + line3);
        writer.close();
        List<String> prospectList = mortagePlan.readFile(file.getAbsolutePath());
        assertTrue(prospectList.get(0).equals(line2));
        assertTrue(prospectList.get(1).equals(line3));
        file.deleteOnExit();
    }


    @Test
    public void testCalculatePower() {
        MortageCalculatorController mortagePlan = new MortageCalculatorController();
        assertEquals(1.0, mortagePlan.calculatePower(1,1));
        assertEquals(1.0, MortageCalculatorController.calculatePower(5,0));
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
