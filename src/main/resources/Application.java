import pyryy;

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

        //right
        Try r = new Try("Right");

        //left
        Try l = new Try("Left");

        //bot
        Try y = new Try("Bot");

        y = x;

        x = m + "sad" + d + m.test(a, c, d) + 1 + true;

        x = m.test1(y) + y;

        x = y + m;

        x = y + 1;

        y = 1;
        y = "sdsda";
        y = true;

        if(x < y && z == 1) {
            x = y;
            y = 1;
            n = a + m.test(y);
        } else {
            if (x) {
                a = z;
            } else {
                d = m.test(y);
            }
        }
    }
}