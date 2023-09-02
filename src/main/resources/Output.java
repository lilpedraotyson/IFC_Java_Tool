class Test {

    public String t;

    public Test(String t) {
        this.t = t;
    }

    public void method1(Test x) {
    }

    public Test method2(Test x, Test y) {
        switch(Math.max(this.level(), Math.max(x.level(), y.level()))) {
            default:
                Test return_statement2 = x;
                return new Test(return_statement2.t);
            case 0:
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
        switch(Math.max(this.level(), Math.max(x.level(), y.level()))) {
            case 1:
                Test return_statement0 = x;
                return new Test(return_statement0.t);
            default:
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
        Test y_1 = new Test(y.t);
        //declassification(y, High) {
        x = y_1;
        y_1 = x;
        y_1.method1(y_1);
        y_1 = (Test_Low) x.method2(y_1, x);
        //}
        x = y;
        y = x;
    }
}
