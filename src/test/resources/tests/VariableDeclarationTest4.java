class Test {
    public String t;

    public Test(String t) {this.t = t;}
}

class Test1 {
    public String t;

    public Test1(String t) {this.t = t;}
}

class Test2 {
    public String t;

    public Test2(String t) {this.t = t;}
}

public class Application {
    public static void main(String[] args) {
        //top
        Test x = new Test("top");
        //mid
        Test1 m = new Test1("mid");
        //bot
        Test2 b = new Test2("bot");

        //mid
        Test m1 = new Test("mid");
        //bot
        Test2 b1 = new Test2("bot");
        //top
        Test2 x1 = new Test2("top");
        //top
        Test1 x2 = new Test1("top");
    }
}