# MortagePlan
Coding assignment for Crosskey job interview

Tools used are: Maven, Spring Boot, H2 in-memory DB and JUnit. Didn't have experience building web applications with Java before so I used 
Spring Boot since there were simple tutorials and plenty of documentation I could apply to this application, it also seems to be used
heavily in the industry.

To use:
Clone or download the project to a folder and with Maven and JDK installed navigate to the directory and type: mvn test, into a command line of choice
to run the tests. To execute the program run: mvn spring-boot:run. Open a browser and go to localhost:8080 to see the beautiful web interface with no CSS. Ctrl + C to terminate the program.

Add prospect - obviously adds the prospect/customer to the database if you fill in the required information

Add original prospects - adds the customers/prospects from the txt file to the DB

List prospects - returns a list of all customers/prospects in db

Calculate mortage - returns a string which says how much each prospect/customer needs to pay monthly to pay off their loan in the desired amount of time

To create a runnable Jar run the command: mvn clean install in the directory and go to the newly created target folder and run: java -jar MortageCalculator-0.0.1-SNAPSHOT.jar or java -jar MortageCalculator-0.0.1, whichever works for you.
