# Internship Placement Management Application

## Quick Start
Before running the programme, ensure the following directory exists at the project root:
```
data/
```

Inside the directory, the system will read / write the following files:
```
data/
    users.ser
    internships.ser
    internshipApplications.ser
```

### Using Pre-Loaded Demo Data
To follow through our demonstration, you can use our pre-populated data of Users, Internships and InternshipApplications.
1. Delete any existing `.ser` files inside `data`
2. Run the following class in your IDE:
```
temp/BootstrapData.java
```
This will auto-generate sample CareerCentreStaff, CompanyRep, Students, Internships and InternshipApplications.

### How to Run the Application
Via IDE
1. Open the project folder in your IDE	
2. Navigate to `boundary/MainApp.java`
3. Run the main() method