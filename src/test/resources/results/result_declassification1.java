class Test {

    public String t;

    public Test(String t) {
        this.t = t;
    }

    public void method1(Test x) {
    }

    public Test method2(Test x) {
        switch(Math.max(this.level(), x.level())) {
            case 2:
                Test return_statement12 = x;
                return new Test(return_statement12.t);
            case 0:
                Test return_statement13 = x;
                return new Test_bot(return_statement13.t);
            case 1:
                Test return_statement14 = x;
                return new Test_mid(return_statement14.t);
        }
    }

    public Test method3(Test x, Test y) {
        switch(Math.max(this.level(), Math.max(x.level(), y.level()))) {
            case 2:
                Test return_statement15 = x;
                return new Test(return_statement15.t);
            case 0:
                Test return_statement16 = x;
                return new Test_bot(return_statement16.t);
            case 1:
                Test return_statement17 = x;
                return new Test_mid(return_statement17.t);
        }
    }

    public int level() {
        return 2;
    }
}

class Test_mid extends Test {

    public Test_mid(String t) {
        super(t);
    }

    @Override()
    public int level() {
        return 1;
    }

    @Override()
    public void method1(Test x) {
    }

    @Override()
    public Test method2(Test x) {
        switch(Math.max(this.level(), x.level())) {
            case 2:
                Test return_statement6 = x;
                return new Test(return_statement6.t);
            case 0:
                Test return_statement7 = x;
                return new Test_bot(return_statement7.t);
            case 1:
                Test return_statement8 = x;
                return new Test_mid(return_statement8.t);
        }
    }

    @Override()
    public Test method3(Test x, Test y) {
        switch(Math.max(this.level(), Math.max(x.level(), y.level()))) {
            case 2:
                Test return_statement9 = x;
                return new Test(return_statement9.t);
            case 0:
                Test return_statement10 = x;
                return new Test_bot(return_statement10.t);
            case 1:
                Test return_statement11 = x;
                return new Test_mid(return_statement11.t);
        }
    }
}

class Test_bot extends Test_mid {

    public Test_bot(String t) {
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
        switch(Math.max(this.level(), x.level())) {
            case 2:
                Test return_statement0 = x;
                return new Test(return_statement0.t);
            case 0:
                Test return_statement1 = x;
                return new Test_bot(return_statement1.t);
            case 1:
                Test return_statement2 = x;
                return new Test_mid(return_statement2.t);
        }
    }

    @Override()
    public Test method3(Test x, Test y) {
        switch(Math.max(this.level(), Math.max(x.level(), y.level()))) {
            case 2:
                Test return_statement3 = x;
                return new Test(return_statement3.t);
            case 0:
                Test return_statement4 = x;
                return new Test_bot(return_statement4.t);
            case 1:
                Test return_statement5 = x;
                return new Test_mid(return_statement5.t);
        }
    }
}

public class Application {

    public static void main(String[] args) {
        //top
        Test x = new Test("top");
        //mid
        Test_mid m = new Test_mid("mid");
        //bot
        Test_bot b = new Test_bot("bot");
        x = b;
        m = x;
        x = m;
        x = (Test) m.method3(m, b);
        b = (Test_bot) x.method2(m);
        Test_bot x_1 = new Test_bot(x.t);
        //declassification(x, bot) {
        x_1 = b;
        m = x_1;
        Test_mid x_2 = new Test_mid(x.t);
        //declassification(x, mid) {
        x_2 = m;
        Test_high m_3 = new Test_high(m.t);
        //declassification(m, high) {
        x_2 = (Test) m_3.method3(m_3, b);
        //}
        b = (Test_bot) x_2.method2(m);
        //}
        x_1 = b;
        m = x_1;
        x_1 = m;
        //}
        x = (Test) m.method3(m, b);
        b = (Test_bot) x.method2(m);
    }
}
