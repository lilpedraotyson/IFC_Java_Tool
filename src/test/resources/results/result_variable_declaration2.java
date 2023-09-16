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

public class Application {

    public static void main(String[] args) {
        //top
        Test x = new Test("top");
        //mid
        Test_mid m = new Test_mid("mid");
        //bot
        Test_bot b = new Test_bot("bot");
    }
}
