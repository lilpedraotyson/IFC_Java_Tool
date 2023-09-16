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

class Test1 {

    public String t;

    public Test1(String t) {
        this.t = t;
    }

    public int level() {
        return 1;
    }
}

class Test1_low extends Test1 {

    public Test1_low(String t) {
        super(t);
    }

    @Override()
    public int level() {
        return 0;
    }
}

class Test2 {

    public String t;

    public Test2(String t) {
        this.t = t;
    }

    public int level() {
        return 1;
    }
}

class Test2_low extends Test2 {

    public Test2_low(String t) {
        super(t);
    }

    @Override()
    public int level() {
        return 0;
    }
}

public class Application {

    public static void main(String[] args) {
    }
}