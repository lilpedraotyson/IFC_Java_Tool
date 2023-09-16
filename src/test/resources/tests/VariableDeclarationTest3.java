class Test {
    public String t;

    public Test(String t) {this.t = t;}
}

public class Application {
    public static void main(String[] args) {
        //top
        Test x = new Test("top");
        //mid
        Test m = new Test("mid");
        //bot
        Test b = new Test("bot");

        //mid
        Test m1 = new Test("mid");
        //bot
        Test b1 = new Test("bot");
        //top
        Test x1 = new Test("top");
        //top
        Test x2 = new Test("top");
    }
}