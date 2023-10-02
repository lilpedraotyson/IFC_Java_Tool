class Test {
    public String t;

    public Test(String t) {this.t = t;}

    public void method1(Test x) {}
    public Test method2(Test x, Test y) {
        //High
        Test b = new Test("High");

        //Low
        Test a = new Test("Low");

        x = y;
        y = x;

        a = x;
        b = y;

        a = x.method2(a, b);

        y = a.method2(a, b);

        //declassification(y, Low) {
        x = y;
        y = x;
        //declassification(y, High) {
        y.method1(y);
        y = x.method2(y, x);
        y = a;
        x = b;
        x = y.method2(a, b);
        //}
        y = x.method2(a, b);
        x = y;
        y = b;
        //}

        return x;

    }
}

public class Application {
    public static void main(String[] args) {
        //High
        Test x = new Test("High");

        //Low
        Test y = new Test("Low");

        x = y;
        y = x;

        y.method1(y);
        y = x.method2(x, y);

        //declassification(y, High) {
        x = y;
        y = x;
        //declassification(x, High) {
        y.method1(y);
        y = x.method2(y, x);
        //}
        x = y;
        y = x;
        //}
    }
}