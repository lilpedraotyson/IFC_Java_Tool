class Test {

    public String t;

    public Test(String t) {
        this.t = t;
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
}

class Test_bot extends Test_mid {

    public Test_bot(String t) {
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
        return 2;
    }
}

class Test1_mid extends Test1 {

    public Test1_mid(String t) {
        super(t);
    }

    @Override()
    public int level() {
        return 1;
    }
}

class Test1_bot extends Test1_mid {

    public Test1_bot(String t) {
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
        return 2;
    }
}

class Test2_mid extends Test2 {

    public Test2_mid(String t) {
        super(t);
    }

    @Override()
    public int level() {
        return 1;
    }
}

class Test2_bot extends Test2_mid {

    public Test2_bot(String t) {
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
