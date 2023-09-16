class Test {
    public String t;

    public Test(String t) {this.t = t;}

    public void method1(Test x) {}

    public Test method2(Test x) {
        return x;
    }

    public Test method3(Test x, Test y) {
        return x;
    }
}

class Test1 {
    public String t;

    public Test(String t) {this.t = t;}

    public Test method5(Test x) {
        return x;
    }
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
        Test1 m1 = new Test1("mid");

        x = b;
        b = m;
        m = x;
        x = m;
        m1 = b;

        x = m.method3(m, b);
        b = x.method2(m);
        m1 = m1.method5(m1);
    }
}