package siokouros.filippos.phonepark.Model;


public class Car {



    private String make;
    private String model;
    private String year;
    private String colour;
    private String RegNumber;

    public Car(String make, String model, String year, String colour, String regNumber) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.colour = colour;
        RegNumber = regNumber;
    }

    public Car() {

    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getRegNumber() {
        return RegNumber;
    }

    public void setRegNumber(String regNumber) {
        RegNumber = regNumber;
    }
}
