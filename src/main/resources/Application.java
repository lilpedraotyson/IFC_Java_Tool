class Try {
    public String t;

    public Try(String t) {this.t = t;}

    public void test1(Try x) {
        System.out.println("entrei1");
        System.out.println(x.toString());
    }
}

class Try1 {
    public String t1;
    public int t2;
    public static final boolean t3;
    public String t4;

    public Try1(String t) {this.t = t;}

    public Try1(String t, String t1) {
        this.t = t;
        this.t1 = t1;
        if (t == t1) {
            this.t2 = t1;
        }
    }

    public void test1(Try x) {
        System.out.println("entrei1");
        System.out.println(x.toString());
    }
}

public class Application_Linear {
    public static void main(String[] args) {
        //top
        Try x = new Try("Top", 1, "afdasfa");

        //mid
        Try m = new Try("Middle");

        //bot
        Try y = new Try("Bot");

        y = x;

        x = m + x + c + v + b + d;

        x = m.test1(y) + y;

        x = y + m;
    }
}