package com.crosskey.mortagecalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

@RestController
public class MortageCalculatorController {

    private static DecimalFormat df = new DecimalFormat("0.00");
    private Iterable<Prospect> prospects;

    @Autowired
    public ProspectRepository prospectRepository;

    @Autowired
    ResourceLoader resourceLoader;

    @PostMapping("/add")
    public String addProspect(@RequestParam String name, @RequestParam double totalLoan ,@RequestParam double interest, @RequestParam int years){
        Prospect prospect = new Prospect();
        prospect.setName(name);
        prospect.setInterest(interest);
        prospect.setTotalLoan(totalLoan);
        prospect.setYears(years);
        calculateMortage(prospect);
        prospectRepository.save(prospect);
        return "New Prospect added to DB!";
    }


    @GetMapping("/list")
    public Iterable<Prospect> getCustomers() {
        prospects = prospectRepository.findAll();
        return prospects;
    }


    //read original prospects to a list
    public List<String> readFile(BufferedReader reader) {
        List<String> prospectList = new ArrayList<String>();
        try {
            //skip first line
            reader.readLine();
            String line ="";
            while ((line = reader.readLine()) != null) {
                // clean up txt file a little
                if(!line.isBlank() && !line.isEmpty() && !line.equals("") && !line.equals(".")){
                    prospectList.add(line);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prospectList;
    }

    //Fill database with prospects from original txt file.
    @GetMapping("/populate")
    public String startPopulating() {
        //just getting the path or getResource won't work when we create a JAR
        InputStream inputStream = MortageCalculatorController.class.getResourceAsStream("/prospects.txt");
        InputStreamReader inputReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputReader);
        reader.lines();
        System.out.println(inputReader.toString());
        List<String> prospectList = readFile(reader);
        populateDatabase(prospectList);
        return "Prospects from text file added to database!";
    }

    public void populateDatabase(List<String> prospectList) {
        for(String s: prospectList){
            //regex expression found online for splitting by commas except for commas inside ""
            String[] tokens = s.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            String name = tokens[0];
            double totalLoan = Double.parseDouble(tokens[1]);
            double interest = Double.parseDouble(tokens[2]);
            int years = Integer.parseInt(tokens[3]);
            addProspect(name, totalLoan, interest, years);
        }
    }


    //iterating through database and adding monthly mortage to db
    @GetMapping("/calculate")
    public String calculate() {
        prospects = getCustomers();
        for(Prospect p: prospects){
            double mortage = calculateMortage(p);
        }
        return printMortagePlan();
    }


    public String printMortagePlan(){
        prospects = getCustomers();
        String result = "";
        int prospectNumber = 1;
        for(Prospect p: prospects){
            result=result + System.lineSeparator() + "Prospect " + prospectNumber + ": " + p.getName() +
                    " wants to borrow " + p.getTotalLoan() + " euros for a period of " + p.getYears() +
                    " years and pay " + df.format(p.getMonthlyMortage()) + " euros each month";
            prospectNumber ++;
        }
        System.out.print(result);
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
    // we're actually not doing with the return value but it helps with creating the JUnit tests
    public Double calculateMortage(Prospect prospect) {
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
        prospect.setMonthlyMortage(E);
        return E;
    }

}
