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
    }
}