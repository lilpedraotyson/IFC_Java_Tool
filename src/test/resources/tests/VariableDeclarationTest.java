class Test {
    public String t;

    public Test(String t) {this.t = t;}
}

public class Application {
    public static void main(String[] args) {
        //high
        Test x = new Test("high");
        //low
        Test m = new Test("low");
    }
}