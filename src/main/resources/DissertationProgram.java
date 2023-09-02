class Test {
    public String t;

    public Test(String t) {this.t = t;}

    public void method1(Test x) {}
    public Test method2(Test x, Test y) {
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
        y.method1(y);
        y = x.method2(y, x);
        //}
        x = y;
        y = x;
    }
}