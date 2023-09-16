class Test {

    public String t;

    public Test(String t) {
        this.t = t;
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
}

public class Application {

    public static void main(String[] args) {
        //low
        Test_low x = new Test_low("low");
        //high
        Test m = new Test("high");
    }
}
