package tvt;

import config.config;
import java.util.Scanner;

public class main {
    private static config dbConfig = new config();
    private static Scanner scanner = new Scanner(System.in);
    private static String currentUser = "";
    private static boolean isAdmin = false;
    
    public static void main(String[] args) {
        int option = 0;
        
        System.out.println("================================================");
        System.out.println("    TRAFFIC VIOLATION TRACKER SYSTEM");
        System.out.println("================================================");
        
        do {
            System.out.println("\n====== MAIN MENU ======\n");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            
            try {
                option = scanner.nextInt();
                scanner.nextLine(); 
                
                switch(option) { 
                    case 1:
                        loginUser();
                        break;
                    case 2:
                        registerUser();
                        break;
                    case 3:
                        System.out.println("\nThank you for using Traffic Violation Tracker!");
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option! Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); 
            }
            
        } while (option != 3);
        
        scanner.close();
    }
    
    private static void loginUser() {
        System.out.println("\n====== USER LOGIN ======\n");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Error: Username and password cannot be empty!");
            return;
        }
        
        String hashedPassword = dbConfig.hashPassword(password);
        String sql = "SELECT * FROM tbl_user WHERE u_user = ? AND u_pass = ?";
        java.util.List<java.util.Map<String, Object>> results = dbConfig.fetchRecords(sql, username, hashedPassword);
        
        if (!results.isEmpty()) {
            currentUser = username;
            
            Object status = results.get(0).get("u_status");
            isAdmin = (status != null && status.toString().equals("1"));
            
            System.out.println("\nLogin successful! Welcome, " + username);
            if (isAdmin) {
                System.out.println("Administrator Access Granted");
            } else {
                System.out.println("Regular User Access Granted");
            }
            showMainMenu();
        } else {
            System.out.println("Invalid username or password!");
        }
    }
    
    private static void registerUser() {
        System.out.println("\n====== USER REGISTRATION ======\n");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Error: Username and password cannot be empty!");
            return;
        }
        
        if (username.length() < 3) {
            System.out.println("Error: Username must be at least 3 characters long!");
            return;
        }
        
        if (password.length() < 4) {
            System.out.println("Error: Password must be at least 4 characters long!");
            return;
        }
        
        String checkSQL = "SELECT * FROM tbl_user WHERE u_user = ?";
        java.util.List<java.util.Map<String, Object>> existingUsers = dbConfig.fetchRecords(checkSQL, username);
        
        if (!existingUsers.isEmpty()) {
            System.out.println("Error: Username already exists! Please choose another username.");
            return;
        }
        
        String hashedPassword = dbConfig.hashPassword(password);
        
        String insertSQL = "INSERT INTO tbl_user (u_user, u_pass, u_status) VALUES (?, ?, ?)";
        int result = dbConfig.addRecordAndReturnId(insertSQL, username, hashedPassword, 0);
        
        if (result != -1) {
            System.out.println("Registration successful! You can now login.");
        } else {
            System.out.println("Registration failed! Please try again.");
        }
    }

    private static void showMainMenu() {
        int choice = 0;
        do {
            System.out.println("\n====== DASHBOARD - Welcome " + currentUser + " ======\n");
            System.out.println("1. Input New Traffic Violator");
            System.out.println("2. View All Traffic Violators");
            System.out.println("3. Search Traffic Violator");
            System.out.println("4. View Vehicle Information");
            
            if (isAdmin) {
                System.out.println("5. Update Traffic Violator");
                System.out.println("6. Delete Traffic Violator");
                System.out.println("7. Manage Vehicle Information");
                System.out.println("8. Update My Password");
                System.out.println("9. Log out");
            } else {
                System.out.println("5. Update My Password");
                System.out.println("6. Log out");
            }
            
            System.out.print("Choose option: ");
            
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
                
                if (isAdmin) {
                    switch(choice) {
                        case 1:
                            inputNewViolator();
                            break;
                        case 2:
                            viewAllViolators();
                            break;
                        case 3:
                            searchViolator();
                            break;
                        case 4:
                            viewAllVehicles();
                            break;
                        case 5:
                            updateViolator();
                            break;
                        case 6:
                            deleteViolator();
                            break;
                        case 7:
                            manageVehicleInfo();
                            break;
                        case 8:
                            updateMyPassword();
                            break;
                        case 9:
                            System.out.println("Logging out...");
                            currentUser = "";
                            isAdmin = false;
                            break;
                        default:
                            System.out.println("Invalid option! Please try again.");
                    }
                } else {
                    switch(choice) {
                        case 1:
                            inputNewViolator();
                            break;
                        case 2:
                            viewAllViolators();
                            break;
                        case 3:
                            searchViolator();
                            break;
                        case 4:
                            viewAllVehicles();
                            break;
                        case 5:
                            updateMyPassword();
                            break;
                        case 6:
                            System.out.println("Logging out...");
                            currentUser = "";
                            isAdmin = false;
                            break;
                        default:
                            System.out.println("Invalid option! Please try again.");
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
            }
            
        } while ((isAdmin && choice != 9) || (!isAdmin && choice != 6));
    }
    
    private static void inputNewViolator() {
        System.out.println("\n====== INPUT NEW TRAFFIC VIOLATOR ======\n");
        
        System.out.print("Enter Violator ID: ");
        String violatorId = scanner.nextLine();
        
        System.out.print("Enter Violator Name: ");
        String violatorName = scanner.nextLine();
        
        System.out.print("Enter License Number: ");
        String licenseNumber = scanner.nextLine();
        
        System.out.print("Enter Vehicle Make: ");
        String vehicleMake = scanner.nextLine();
        
        System.out.print("Enter Violation: ");
        String violation = scanner.nextLine();
        
        if (violatorId.isEmpty() || violatorName.isEmpty() || licenseNumber.isEmpty() || 
            vehicleMake.isEmpty() || violation.isEmpty()) {
            System.out.println("Error: All fields are required!");
            return;
        }
        
        String checkSQL = "SELECT * FROM tbl_violators WHERE v_id = ?";
        java.util.List<java.util.Map<String, Object>> existingViolators = dbConfig.fetchRecords(checkSQL, violatorId);
        
        if (!existingViolators.isEmpty()) {
            System.out.println("Error: Violator ID already exists!");
            return;
        }
        
        String insertSQL = "INSERT INTO tbl_violators (v_id, v_name, v_lice, v_vehi, v_viol) VALUES (?, ?, ?, ?, ?)";
        int result = dbConfig.addRecordAndReturnId(insertSQL, violatorId, violatorName, licenseNumber, vehicleMake, violation);
        
        if (result != -1) {
            System.out.println("Traffic violator added successfully!");
        } else {
            System.out.println("Failed to add traffic violator!");
        }
    }
    
    private static void viewAllViolators() {
        System.out.println("\n====== ALL TRAFFIC VIOLATORS ======\n");
        
        String[] headers = {"Violator ID", "Name", "License No", "Vehicle Make", "Violation"};
        String[] columns = {"v_id", "v_name", "v_lice", "v_vehi", "v_viol"};
        String sql = "SELECT * FROM tbl_violators ORDER BY v_id";
        
        try {
            dbConfig.viewRecords(sql, headers, columns);
        } catch (Exception e) {
            System.out.println("Error displaying violators: " + e.getMessage());
        }
    }
    
    private static void searchViolator() {
        System.out.println("\n====== SEARCH TRAFFIC VIOLATOR ======\n");
        System.out.print("Enter Violator ID or Name to search: ");
        String searchTerm = scanner.nextLine();
        
        String sql = "SELECT * FROM tbl_violators WHERE v_id LIKE ? OR v_name LIKE ? ORDER BY v_id";
        java.util.List<java.util.Map<String, Object>> results = dbConfig.fetchRecords(sql, 
            "%" + searchTerm + "%", "%" + searchTerm + "%");
        
        if (!results.isEmpty()) {
            System.out.println("\n✓ Found " + results.size() + " violator(s):");
            System.out.println("==================================================================");
            System.out.printf("%-12s %-20s %-15s %-15s %s\n", 
                "Violator ID", "Name", "License No", "Vehicle Make", "Violation");
            System.out.println("==================================================================");
            
            for (java.util.Map<String, Object> violator : results) {
                System.out.printf("%-12s %-20s %-15s %-15s %s\n",
                    violator.get("v_id"),
                    violator.get("v_name"),
                    violator.get("v_lice"),
                    violator.get("v_vehi"),
                    violator.get("v_viol"));
            }
            System.out.println("==================================================================");
        } else {
            System.out.println("No violators found matching your search.");
        }
    }
    
    private static void updateViolator() {
        if (!isAdmin) {
            System.out.println("Access denied! Administrator privileges required to update violators.");
            return;
        }
        
        System.out.println("\n====== UPDATE TRAFFIC VIOLATOR ======\n");
        System.out.print("Enter Violator ID to update: ");
        String violatorId = scanner.nextLine();
        
        String checkSQL = "SELECT * FROM tbl_violators WHERE v_id = ?";
        java.util.List<java.util.Map<String, Object>> violator = dbConfig.fetchRecords(checkSQL, violatorId);
        
        if (violator.isEmpty()) {
            System.out.println("Error: Violator not found!");
            return;
        }
        
        System.out.println("\nCurrent Information:");
        System.out.println("1. Name: " + violator.get(0).get("v_name"));
        System.out.println("2. License: " + violator.get(0).get("v_lice"));
        System.out.println("3. Vehicle: " + violator.get(0).get("v_vehi"));
        System.out.println("4. Violation: " + violator.get(0).get("v_viol"));
        
        System.out.println("\nEnter new information (press enter to keep current value):");
        
        System.out.print("New Name: ");
        String newName = scanner.nextLine();
        
        System.out.print("New License Number: ");
        String newLicense = scanner.nextLine();
        
        System.out.print("New Vehicle Make: ");
        String newVehicle = scanner.nextLine();
        
        System.out.print("New Violation: ");
        String newViolation = scanner.nextLine();
        
        if (newName.isEmpty()) newName = violator.get(0).get("v_name").toString();
        if (newLicense.isEmpty()) newLicense = violator.get(0).get("v_lice").toString();
        if (newVehicle.isEmpty()) newVehicle = violator.get(0).get("v_vehi").toString();
        if (newViolation.isEmpty()) newViolation = violator.get(0).get("v_viol").toString();
        
        String updateSQL = "UPDATE tbl_violators SET v_name = ?, v_lice = ?, v_vehi = ?, v_viol = ? WHERE v_id = ?";
        dbConfig.updateRecord(updateSQL, newName, newLicense, newVehicle, newViolation, violatorId);
        
        System.out.println("Violator information updated successfully!");
    }
    
    private static void deleteViolator() {
        if (!isAdmin) {
            System.out.println("Access denied! Administrator privileges required to delete violators.");
            return;
        }
        
        System.out.println("\n====== DELETE TRAFFIC VIOLATOR ======\n");
        System.out.print("Enter Violator ID to delete: ");
        String violatorId = scanner.nextLine();
       
        String checkSQL = "SELECT * FROM tbl_violators WHERE v_id = ?";
        java.util.List<java.util.Map<String, Object>> violator = dbConfig.fetchRecords(checkSQL, violatorId);
        
        if (violator.isEmpty()) {
            System.out.println("Error: Violator not found!");
            return;
        }
     
        System.out.println("\nViolator Details:");
        System.out.println("ID: " + violator.get(0).get("v_id"));
        System.out.println("Name: " + violator.get(0).get("v_name"));
        System.out.println("License: " + violator.get(0).get("v_lice"));
        System.out.println("Vehicle: " + violator.get(0).get("v_vehi"));
        System.out.println("Violation: " + violator.get(0).get("v_viol"));
        
        System.out.print("\nAre you sure you want to delete this violator? (yes/no): ");
        String confirmation = scanner.nextLine();
        
        if (confirmation.equalsIgnoreCase("yes")) {
            String deleteSQL = "DELETE FROM tbl_violators WHERE v_id = ?";
            dbConfig.deleteRecord(deleteSQL, violatorId);
            System.out.println("Violator deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
   
    private static void updateMyPassword() {
        System.out.println("\n====== UPDATE MY PASSWORD ======\n");
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine();
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine();
        
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Error: New passwords don't match!");
            return;
        }
        
        if (newPassword.length() < 4) {
            System.out.println("Error: New password must be at least 4 characters long!");
            return;
        }
        
        String currentHashed = dbConfig.hashPassword(currentPassword);
        String verifySQL = "SELECT * FROM tbl_user WHERE u_user = ? AND u_pass = ?";
        java.util.List<java.util.Map<String, Object>> user = dbConfig.fetchRecords(verifySQL, currentUser, currentHashed);
        
        if (user.isEmpty()) {
            System.out.println("Error: Current password is incorrect!");
            return;
        }
        
        String newHashed = dbConfig.hashPassword(newPassword);
        String updateSQL = "UPDATE tbl_user SET u_pass = ? WHERE u_user = ?";
        dbConfig.updateRecord(updateSQL, newHashed, currentUser);
        
        System.out.println("Password updated successfully!");
    }

    private static void manageVehicleInfo() {
        if (!isAdmin) {
            System.out.println("Access denied! Administrator privileges required to manage vehicle information.");
            return;
        }
        
        System.out.println("\n====== MANAGE VEHICLE INFORMATION ======\n");
        System.out.println("1. Add Vehicle Information");
        System.out.println("2. View All Vehicles");
        System.out.println("3. Back to Main Menu");
        System.out.print("Choose option: ");
        
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch(choice) {
                case 1:
                    addVehicleInfo();
                    break;
                case 2:
                    viewAllVehicles();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        } catch (Exception e) {
            System.out.println("Invalid input!");
            scanner.nextLine();
        }
    }

    private static void addVehicleInfo() {
        if (!isAdmin) {
            System.out.println("Access denied! Administrator privileges required to add vehicle information.");
            return;
        }
        
        System.out.println("\n====== ADD VEHICLE INFORMATION ======\n");
        
        System.out.print("Enter Vehicle Plate: ");
        String vPlate = scanner.nextLine();
        
        System.out.print("Enter Vehicle Model: ");
        String vModel = scanner.nextLine();
        
        System.out.print("Enter Vehicle Type: ");
        String vType = scanner.nextLine();
        
        System.out.print("Enter Registration Number: ");
        String vRegistration = scanner.nextLine();
        
        System.out.print("Enter Associated Violator ID: ");
        String vId = scanner.nextLine();
        
        if (vPlate.isEmpty() || vModel.isEmpty() || vType.isEmpty() || vRegistration.isEmpty() || vId.isEmpty()) {
            System.out.println("Error: All fields are required!");
            return;
        }
        
        String checkViolatorSQL = "SELECT * FROM tbl_violators WHERE v_id = ?";
        java.util.List<java.util.Map<String, Object>> violator = dbConfig.fetchRecords(checkViolatorSQL, vId);
        
        if (violator.isEmpty()) {
            System.out.println("Error: Violator ID does not exist!");
            return;
        }
        
        String insertSQL = "INSERT INTO tbl_vehicle (v_plate, v_model, v_type, v_registration, v_id) VALUES (?, ?, ?, ?, ?)";
        int result = dbConfig.addRecordAndReturnId(insertSQL, vPlate, vModel, vType, vRegistration, vId);
        
        if (result != -1) {
            System.out.println("Vehicle information added successfully!");
        } else {
            System.out.println("Failed to add vehicle information!");
        }
    }

    private static void viewAllVehicles() {
        System.out.println("\n====== ALL VEHICLES ======\n");
        
        String sql = "SELECT veh.v_plate, veh.v_model, veh.v_type, veh.v_registration, veh.v_id, viol.v_name FROM tbl_vehicle veh LEFT JOIN tbl_violators viol ON veh.v_id = viol.v_id ORDER BY veh.v_plate";
        
        String[] headers = {"Plate No", "Model", "Type", "Registration", "Violator ID", "Violator Name"};
        String[] columns = {"v_plate", "v_model", "v_type", "v_registration", "v_id", "v_name"};
        
        try {
            dbConfig.viewRecords(sql, headers, columns);
        } catch (Exception e) {
            System.out.println("Error displaying vehicles: " + e.getMessage());
        }
    }
}