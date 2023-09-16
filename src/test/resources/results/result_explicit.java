class Test {

    public String t;

    public Test(String t) {
        this.t = t;
    }

    public void method1(Test x) {
    }

    public Test method2(Test x) {
        switch(Math.min(this.level(), x.level())) {
            case 0:
                Test return_statement4 = x;
                return new Test_low(return_statement4.t);
            default:
                Test return_statement5 = x;
                return new Test(return_statement5.t);
        }
    }

    public Test method3(Test x, Test y) {
        switch(Math.min(this.level(), Math.min(x.level(), y.level()))) {
            case 0:
                Test return_statement6 = x;
                return new Test_low(return_statement6.t);
            default:
                Test return_statement7 = x;
                return new Test(return_statement7.t);
        }
    }

    public int level() {
        return 1;
    }
}

class Test_low extends Test {

    public Test_low(String t) {
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
    public Test method2(Test x) {
        switch(Math.min(this.level(), x.level())) {
            case 1:
                Test return_statement0 = x;
                return new Test(return_statement0.t);
            default:
                Test return_statement1 = x;
                return new Test_low(return_statement1.t);
        }
    }

    @Override()
    public Test method3(Test x, Test y) {
        switch(Math.min(this.level(), Math.min(x.level(), y.level()))) {
            case 1:
                Test return_statement2 = x;
                return new Test(return_statement2.t);
            default:
                Test return_statement3 = x;
                return new Test_low(return_statement3.t);
        }
    }
}

public class Application {

    public static void main(String[] args) {
    }
}