package XMLparsing;

public class Star {
    private String name;
    private int birthYear;
    
    public Star() {
        birthYear = 0;
    }
    
    public Star(String name, int birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }
    
    public String getStarName() {
        return name;
    }
    
    public int getBirthYear() {
        return birthYear;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }
    
    public String toString() {
        return String.format("Star(%s, %d)", name, birthYear);
    }
}
