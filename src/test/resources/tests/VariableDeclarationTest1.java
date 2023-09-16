class Test {
    public String t;

    public Test(String t) {this.t = t;}
}

public class Application {
    public static void main(String[] args) {
        //low
        Test x = new Test("low");
        //high
        Test m = new Test("high");
    }
}