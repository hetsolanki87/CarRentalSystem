import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class CarRentalSystem {
    public static void main(String[] args) {
        RentalService rentalService = new RentalService();
        Scanner scanner = new Scanner(System.in);
        
        // Add some sample vehicles
        rentalService.addVehicle(new Vehicle("Toyota", "Corolla", 2022, "Economy", 5000.0));
        rentalService.addVehicle(new Vehicle("Honda", "City", 2023, "Economy", 5500.0));
        rentalService.addVehicle(new Vehicle("Ford", "Focus", 2022, "Economy", 4500.0));
        rentalService.addVehicle(new Vehicle("BMW", "GT", 2023, "Luxury", 12000.0));
        rentalService.addVehicle(new Vehicle("Mercedes", "E-Class", 2023, "Luxury", 13000.0));
        rentalService.addVehicle(new Vehicle("Ford", "Transit", 2022, "Van", 8000.0));
        
        boolean running = true;
        while (running) {
            System.out.println("\n=== Car Rental System ===");
            System.out.println("1. Register Customer");
            System.out.println("2. View Available Vehicles");
            System.out.println("3. Rent a Vehicle");
            System.out.println("4. Return a Vehicle");
            System.out.println("5. View All Rentals");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    registerCustomer(scanner, rentalService);
                    break;
                case 2:
                    viewAvailableVehicles(rentalService);
                    break;
                case 3:
                    rentVehicle(scanner, rentalService);
                    break;
                case 4:
                    returnVehicle(scanner, rentalService);
                    break;
                case 5:
                    viewAllRentals(rentalService);
                    break;
                case 6:
                    running = false;
                    System.out.println("Thank you for using Car Rental System!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        scanner.close();
    }
    
    private static void registerCustomer(Scanner scanner, RentalService rentalService) {
        System.out.println("\n=== Register Customer ===");
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter license number: ");
        String licenseNumber = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine();
        
        Customer customer = new Customer(name, licenseNumber, phoneNumber);
        rentalService.addCustomer(customer);
        
        System.out.println("Customer registered successfully!");
        System.out.println("Customer ID: " + customer.getId());
    }
    
    private static void viewAvailableVehicles(RentalService rentalService) {
        System.out.println("\n=== Available Vehicles ===");
        List<Vehicle> availableVehicles = rentalService.getAvailableVehicles();
        
        if (availableVehicles.isEmpty()) {
            System.out.println("No vehicles are currently available.");
            return;
        }
        
        System.out.printf("%-5s %-12s %-10s %-6s %-10s %-10s\n", "ID", "Make", "Model", "Year", "Type", "Rate/Day");
        System.out.println("-----------------------------------------------------");
        
        for (Vehicle vehicle : availableVehicles) {
            System.out.printf("%-5d %-12s %-10s %-6d %-10s $%-9.2f\n",
                    vehicle.getId(),
                    vehicle.getMake(),
                    vehicle.getModel(),
                    vehicle.getYear(),
                    vehicle.getType(),
                    vehicle.getDailyRate());
        }
    }
    
    private static void rentVehicle(Scanner scanner, RentalService rentalService) {
        System.out.println("\n=== Rent a Vehicle ===");
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine();
        
        Customer customer = rentalService.findCustomerById(customerId);
        if (customer == null) {
            System.out.println("Customer not found. Please register first.");
            return;
        }
        
        viewAvailableVehicles(rentalService);
        
        System.out.print("Enter vehicle ID to rent: ");
        int vehicleId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        Vehicle vehicle = rentalService.findVehicleById(vehicleId);
        if (vehicle == null || !vehicle.isAvailable()) {
            System.out.println("Vehicle not available for rent.");
            return;
        }
        
        System.out.print("Enter rental start date (YYYY-MM-DD): ");
        String startDateStr = scanner.nextLine();
        LocalDate startDate = LocalDate.parse(startDateStr);
        
        System.out.print("Enter rental end date (YYYY-MM-DD): ");
        String endDateStr = scanner.nextLine();
        LocalDate endDate = LocalDate.parse(endDateStr);
        
        RentalAgreement agreement = rentalService.rentVehicle(customer, vehicle, startDate, endDate);
        
        System.out.println("\nRental Confirmed!");
        System.out.println("Rental ID: " + agreement.getId());
        System.out.println("Customer: " + customer.getName());
        System.out.println("Vehicle: " + vehicle.getMake() + " " + vehicle.getModel());
        System.out.println("Total Cost: $" + agreement.getTotalCost());
    }
    
    private static void returnVehicle(Scanner scanner, RentalService rentalService) {
        System.out.println("\n=== Return a Vehicle ===");
        System.out.print("Enter rental ID: ");
        String rentalId = scanner.nextLine();
        
        RentalAgreement agreement = rentalService.findRentalById(rentalId);
        if (agreement == null) {
            System.out.println("Rental agreement not found.");
            return;
        }
        
        if (agreement.isReturned()) {
            System.out.println("This vehicle has already been returned.");
            return;
        }
        
        System.out.print("Enter actual return date (YYYY-MM-DD): ");
        String returnDateStr = scanner.nextLine();
        LocalDate actualReturnDate = LocalDate.parse(returnDateStr);
        
        System.out.print("Enter final fuel level (0.0-1.0): ");
        double fuelLevel = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        
        System.out.print("Enter odometer reading: ");
        double odometerReading = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        
        System.out.print("Any damage to report? (yes/no): ");
        boolean hasDamage = scanner.nextLine().trim().equalsIgnoreCase("yes");
        
        String damageDescription = "";
        if (hasDamage) {
            System.out.print("Enter damage description: ");
            damageDescription = scanner.nextLine();
        }
        
        rentalService.returnVehicle(agreement, actualReturnDate, fuelLevel, odometerReading, hasDamage, damageDescription);
        
        System.out.println("\nVehicle returned successfully!");
        System.out.println("Final Cost: $" + agreement.getTotalCost());
        
        if (agreement.getLateFee() > 0) {
            System.out.println("Late Fee: $" + agreement.getLateFee());
        }
    }
    
    private static void viewAllRentals(RentalService rentalService) {
        System.out.println("\n=== All Rental Agreements ===");
        List<RentalAgreement> agreements = rentalService.getAllRentals();
        
        if (agreements.isEmpty()) {
            System.out.println("No rental agreements found.");
            return;
        }
        
        for (RentalAgreement agreement : agreements) {
            Customer customer = agreement.getCustomer();
            Vehicle vehicle = agreement.getVehicle();
            
            System.out.println("Rental ID: " + agreement.getId());
            System.out.println("Customer: " + customer.getName() + " (ID: " + customer.getId() + ")");
            System.out.println("Vehicle: " + vehicle.getMake() + " " + vehicle.getModel() + " (ID: " + vehicle.getId() + ")");
            System.out.println("Period: " + agreement.getStartDate() + " to " + agreement.getEndDate());
            System.out.println("Status: " + (agreement.isReturned() ? "Returned" : "Active"));
            System.out.println("Total Cost: $" + agreement.getTotalCost());
            System.out.println("-------------------------------");
        }
    }
}

class Vehicle {
    private static int idCounter = 1;
    
    private final int id;
    private final String make;
    private final String model;
    private final int year;
    private final String type;
    private final double dailyRate;
    private boolean available;
    private double odometerReading;
    private double fuelLevel;
    
    public Vehicle(String make, String model, int year, String type, double dailyRate) {
        this.id = idCounter++;
        this.make = make;
        this.model = model;
        this.year = year;
        this.type = type;
        this.dailyRate = dailyRate;
        this.available = true;
        this.odometerReading = 0;
        this.fuelLevel = 1.0; // Full tank
    }
    
    // Getters and setters
    public int getId() { return id; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public String getType() { return type; }
    public double getDailyRate() { return dailyRate; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public double getOdometerReading() { return odometerReading; }
    public void setOdometerReading(double odometerReading) { this.odometerReading = odometerReading; }
    public double getFuelLevel() { return fuelLevel; }
    public void setFuelLevel(double fuelLevel) { this.fuelLevel = fuelLevel; }
}

class Customer {
    private final String id;
    private final String name;
    private final String licenseNumber;
    private final String phoneNumber;
    
    public Customer(String name, String licenseNumber, String phoneNumber) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.phoneNumber = phoneNumber;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getPhoneNumber() { return phoneNumber; }
}

class RentalAgreement {
    private final String id;
    private final Customer customer;
    private final Vehicle vehicle;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private boolean returned;
    private LocalDate actualReturnDate;
    private double totalCost;
    private double lateFee;
    private double finalFuelLevel;
    private double finalOdometerReading;
    private boolean damaged;
    private String damageDescription;
    
    public RentalAgreement(Customer customer, Vehicle vehicle, LocalDate startDate, LocalDate endDate) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.customer = customer;
        this.vehicle = vehicle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.returned = false;
        this.totalCost = calculateInitialCost();
    }
    
    private double calculateInitialCost() {
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1; // Include both start and end days
        return days * vehicle.getDailyRate();
    }
    
    public void processReturn(LocalDate actualReturnDate, double finalFuelLevel, 
                             double finalOdometerReading, boolean damaged, String damageDescription) {
        this.actualReturnDate = actualReturnDate;
        this.finalFuelLevel = finalFuelLevel;
        this.finalOdometerReading = finalOdometerReading;
        this.damaged = damaged;
        this.damageDescription = damageDescription;
        this.returned = true;
        
        // Calculate late fee if any
        if (actualReturnDate.isAfter(endDate)) {
            long extraDays = ChronoUnit.DAYS.between(endDate, actualReturnDate);
            this.lateFee = extraDays * vehicle.getDailyRate() * 1.5; // 50% extra for late returns
            this.totalCost += lateFee;
        }
        
        // Add fuel fee if tank is not full
        if (finalFuelLevel < 1.0) {
            double fuelFee = (1.0 - finalFuelLevel) * 50.0; // $50 for a full tank
            this.totalCost += fuelFee;
        }
        
        // Add damage fee if applicable
        if (damaged) {
            this.totalCost += 200.0; // Basic damage processing fee
        }
    }
    
    // Getters
    public String getId() { return id; }
    public Customer getCustomer() { return customer; }
    public Vehicle getVehicle() { return vehicle; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public boolean isReturned() { return returned; }
    public LocalDate getActualReturnDate() { return actualReturnDate; }
    public double getTotalCost() { return totalCost; }
    public double getLateFee() { return lateFee; }
    public boolean isDamaged() { return damaged; }
    public String getDamageDescription() { return damageDescription; }
} 
class RentalService {
    private final List<Vehicle> vehicles;
    private final Map<String, Customer> customers;
    private final Map<String, RentalAgreement> rentals;
    
    public RentalService() {
        this.vehicles = new ArrayList<>();
        this.customers = new HashMap<>();
        this.rentals = new HashMap<>();
    }
    
    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }
    
    public void addCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
    }
    
    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> availableVehicles = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.isAvailable()) {
                availableVehicles.add(vehicle);
            }
        }
        return availableVehicles;
    }
    
    public Customer findCustomerById(String customerId) {
        return customers.get(customerId);
    }
    
    public Vehicle findVehicleById(int vehicleId) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId() == vehicleId) {
                return vehicle;
            }
        }
        return null;
    }
    
    public RentalAgreement findRentalById(String rentalId) {
        return rentals.get(rentalId);
    }
    
    public RentalAgreement rentVehicle(Customer customer, Vehicle vehicle, LocalDate startDate, LocalDate endDate) {
        vehicle.setAvailable(false);
        RentalAgreement agreement = new RentalAgreement(customer, vehicle, startDate, endDate);
        rentals.put(agreement.getId(), agreement);
        return agreement;
    }
    
    public void returnVehicle(RentalAgreement agreement, LocalDate actualReturnDate, 
                             double finalFuelLevel, double finalOdometerReading, 
                             boolean damaged, String damageDescription) {
        Vehicle vehicle = agreement.getVehicle();
        vehicle.setAvailable(true);
        vehicle.setFuelLevel(finalFuelLevel);
        vehicle.setOdometerReading(finalOdometerReading);
        
        agreement.processReturn(actualReturnDate, finalFuelLevel, finalOdometerReading, damaged, damageDescription);
    }
    
    public List<RentalAgreement> getAllRentals() {
        return new ArrayList<>(rentals.values());
    }
}