class Try {
    public String t;

    public Try(String t) {this.t = t;}

    public void test1(Try x) {
        System.out.println("entrei1");
        System.out.println(x.toString());
    }

    public Try1 test1(Try x) {
        x = z;
        z = c;
        if (x == b) {
            if (x == c) {
                return d;
            }
            return a;
        } else {
            if (x == 3) {
                if (y < 50) {
                    return h;
                }
                return z;
            }
            return b;
        }
        return x;
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

public class Application {
    public static void main(String[] args) {
        //top
        Try x = new Try("Top", 1, "afdasfa");

        //mid
        Try m = new Try("mid", 2, "sadas");

        //bot
        Try y = new Try("Bot");

        this.y = x;

        //mid
        Try z = m + "sad" + d + m.test(a, c, d) + 1 + true;

        //declassification(x, mid) {
        x = m.test1(y) + y;

        x = y + m;

        x = y + 1;

        y = 1;

        //declassification(y, top) {
        y = y + y;
        y = true;
        x = 32;
        if(x < y && z == 1) {
            //}
            x = y;
            //}
            y = 1;
            m = a + m.test(y);
        } else {
            if (x) {
                m = z;
            } else {
                z = m.test(y);
            }
        }
    }
}