class Test {

    public String t;

    public Test(String t) {
        this.t = t;
    }

    public void method1(Test x) {
    }

    public Test method2(Test x, Test y) {
        //High
        Test b = new Test("High");
        //Low
        Test_Low a = new Test_Low("Low");
        Test_Low y_1 = new Test_Low(y.t);
        Test y_2 = new Test(y.t);
        switch(Math.max(this.level(), Math.max(x.level(), y.level()))) {
            case 0:
                x = (Test) y;
                y = (Test) x;
                a = (Test_Low) x;
                b = (Test) y;
                a = (Test_Low) x.method2(a, b);
                y = (Test) a.method2(a, b);
                //declassification(y, Low) {
                x = (Test) y_1;
                y_1 = (Test_Low) x;
                //declassification(y, High) {
                y_2.method1(y_2);
                y_2 = (Test) x.method2(y_2, x);
                y_2 = (Test) a;
                x = (Test) b;
                x = (Test) y_2.method2(a, b);
                //}
                y_1 = (Test_Low) x.method2(a, b);
                x = (Test) y_1;
                y_1 = (Test_Low) b;
                Test return_statement2 = x;
                return new Test_Low(return_statement2.t);
            default:
                x = (Test) y;
                y = (Test) x;
                a = (Test_Low) x;
                b = (Test) y;
                a = (Test_Low) x.method2(a, b);
                y = (Test) a.method2(a, b);
                //declassification(y, Low) {
                x = (Test) y_1;
                y_1 = (Test_Low) x;
                //declassification(y, High) {
                y_2.method1(y_2);
                y_2 = (Test) x.method2(y_2, x);
                y_2 = (Test) a;
                x = (Test) b;
                x = (Test) y_2.method2(a, b);
                //}
                y_1 = (Test_Low) x.method2(a, b);
                x = (Test) y_1;
                y_1 = (Test_Low) b;
                Test return_statement3 = x;
                return new Test(return_statement3.t);
        }
    }

    public int level() {
        return 1;
    }
}

class Test_Low extends Test {

    public Test_Low(String t) {
        super(t);
    }

    @Override()
    public int level() {
        return 0;
    }

    @Override()
    public void method1(Test x) {
    }

    @Override()
    public Test method2(Test x, Test y) {
        //High
        Test b = new Test("High");
        //Low
        Test_Low a = new Test_Low("Low");
        Test_Low y_1 = new Test_Low(y.t);
        Test y_2 = new Test(y.t);
        switch(Math.max(this.level(), Math.max(x.level(), y.level()))) {
            case 1:
                x = (Test) y;
                y = (Test) x;
                a = (Test_Low) x;
                b = (Test) y;
                a = (Test_Low) x.method2(a, b);
                y = (Test) a.method2(a, b);
                //declassification(y, Low) {
                x = (Test) y_1;
                y_1 = (Test_Low) x;
                //declassification(y, High) {
                y_2.method1(y_2);
                y_2 = (Test) x.method2(y_2, x);
                y_2 = (Test) a;
                x = (Test) b;
                x = (Test) y_2.method2(a, b);
                //}
                y_1 = (Test_Low) x.method2(a, b);
                x = (Test) y_1;
                y_1 = (Test_Low) b;
                Test return_statement0 = x;
                return new Test(return_statement0.t);
            default:
                x = (Test) y;
                y = (Test) x;
                a = (Test_Low) x;
                b = (Test) y;
                a = (Test_Low) x.method2(a, b);
                y = (Test) a.method2(a, b);
                //declassification(y, Low) {
                x = (Test) y_1;
                y_1 = (Test_Low) x;
                //declassification(y, High) {
                y_2.method1(y_2);
                y_2 = (Test) x.method2(y_2, x);
                y_2 = (Test) a;
                x = (Test) b;
                x = (Test) y_2.method2(a, b);
                //}
                y_1 = (Test_Low) x.method2(a, b);
                x = (Test) y_1;
                y_1 = (Test_Low) b;
                Test return_statement1 = x;
                return new Test_Low(return_statement1.t);
        }
    }
}

public class Application {

    public static void main(String[] args) {
        //High
        Test x = new Test("High");
        //Low
        Test_Low y = new Test_Low("Low");
        x = y;
        y = x;
        y.method1(y);
        y = (Test_Low) x.method2(x, y);
        //}
        Test y_3 = new Test(y.t);
        //declassification(y, High) {
        x = y_3;
        y_3 = x;
        Test x_4 = new Test(x.t);
        //declassification(x, High) {
        y_3.method1(y_3);
        y_3 = (Test) x_4.method2(y_3, x_4);
        //}
        x = y_3;
        y_3 = x;
    }
}
