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
        //high
        Test x = new Test("high");
        //low
        Test_low m = new Test_low("low");
    }
}
