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

public class Application {
    public static void main(String[] args) {
        //top
        Test x = new Test("top");
        //mid
        Test m = new Test("mid");
        //bot
        Test b = new Test("bot");

        x = b;
        m = x;
        x = m;

        x = m.method3(m, b);
        b = x.method2(m);

        //declassification(x, bot) {
        x = b;
        m = x;
        x = m;

        x = m.method3(m, b);
        b = x.method2(m);
        //}
        x = b;
        m = x;
        x = m;

        x = m.method3(m, b);
        b = x.method2(m);
    }
}