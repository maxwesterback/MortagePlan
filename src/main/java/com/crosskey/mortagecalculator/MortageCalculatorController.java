package com.crosskey.mortagecalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.text.DecimalFormat;

@RestController
public class MortageCalculatorController {

    private static DecimalFormat df = new DecimalFormat("0.00");

    @Autowired
    public ProspectRepository prospectRepository;

    @PostMapping("/add")
    public String addProspect(@RequestParam String name, @RequestParam double totalLoan ,@RequestParam double interest, @RequestParam int years) {
        Prospect prospect = new Prospect();
        prospect.setName(name);
        prospect.setInterest(interest);
        prospect.setTotalLoan(totalLoan);
        prospect.setYears(years);
        prospectRepository.save(prospect);
        return "New Prospect added to DB!";
    }


    @GetMapping("/list")
    public Iterable<Prospect> getCustomers() {
        return prospectRepository.findAll();
    }

    //Fill database with prospects from original txt file.
    @GetMapping("/populate")
    public void populateDB() {
        addProspect("Juha", 1000,5,2);
        addProspect("Karvinen", 4356,1.27,6);
        addProspect("Claes Månsson", 1300.55,8.67,2);
        addProspect("\"Clarencé,Andersson\"", 2000,6,4);
    }


    //iterating through database
    @GetMapping("/calculate")
    public String calculate() throws SQLException {
        Iterable<Prospect> prospects = getCustomers();
        int prospectNumber = 1;
        String result = "";
        for(Prospect p: prospects){
            double mortage = calculateMortage(p);
            result+=("Prospect " + prospectNumber + ": " + p.getName() +
            " wants to borrow " + p.getTotalLoan() + " for a period of " + p.getYears() +
                    " years and pay " + df.format(mortage) + " euros each month\n");
            prospectNumber += 1;
        }
        System.out.println(result);
        return result;
    }


    //since we can't use math.pow
    public static double calculatePower(double a, double b){
        double result = 1;
        for (int i = 0; i < b; i++){
            result = result*a;
        }
        return result;
    }

    //following the formula provided
    public Double calculateMortage(Prospect prospect) throws SQLException {
        //E = U[b(1 + b)^p]/[(1 + b)^p - 1]
        // E = fixed monthly payment
        // b = interest monthly
        // U = total loan
        // p = number of payments
        //E= U[(b*x)/(x - 1)]
        // E = U* y
        Double U = prospect.getTotalLoan();
        Double interest = prospect.getInterest();
        //convert percentage to double
        double a = interest/100;
        // b
        Double b = a/12;
        int years = prospect.getYears();
        // p
        int p = years*12;
        double x = calculatePower((1+b),p);
        double Y = (b*x)/(x-1);
        double E = U * Y;
        return E;
    }

}
