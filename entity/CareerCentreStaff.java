package entity;

public class CareerCentreStaff extends User{
    private String staffDepartment;

    // constructor
    public CareerCentreStaff(String userId, String fullName, String password, String staffDepartment){
        super(userId, fullName, password);
        this.staffDepartment = staffDepartment;
    }

    // getters & setters
    public String getStaffDepartment(){
        return this.staffDepartment;
    }

    public void setStaffDepartment(String staffDepartment){
        this.staffDepartment = staffDepartment;
    }
}