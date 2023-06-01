class Try {

    public String t;

    public Try(String t) {
        this.t = t;
    }

    public void test1(Try x) {
        System.out.println("entrei1");
        System.out.println(x.toString());
    }

    public Try_mid combine(Try x) {
        return new Try_mid(this.t);
    }

    public Try_bot combine(Try_bot x) {
        return new Try_bot(this.t);
    }

    public Try_mid combine(Try_mid x) {
        return new Try_mid(this.t);
    }
}

class Try_mid extends Try_top {

    public Try_mid(String t) {
        super(this.t);
    }

    public Try_mid combine(Try x) {
        return this;
    }

    public Try_bot combine(Try_bot x) {
        return new Try_bot(this.t);
    }

    public Try_mid combine(Try_mid x) {
        return this;
    }
}

class Try_bot extends Try_mid {

    public Try_bot(String t) {
        super(this.t);
    }

    public Try_bot combine(Try x) {
        return this;
    }

    public Try_bot combine(Try_bot x) {
        return this;
    }

    public Try_bot combine(Try_mid x) {
        return this;
    }
}

class Try1 {

    public String t1;

    public int t2;

    public static final boolean t3;

    public String t4;

    public Try1(String t) {
        this.t = t;
    }

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

    public Try1_mid combine(Try1 x) {
        return new Try1_mid(this.t1, this.t2, this.t3, this.t4);
    }

    public Try1_bot combine(Try1_bot x) {
        return new Try1_bot(this.t1, this.t2, this.t3, this.t4);
    }

    public Try1_mid combine(Try1_mid x) {
        return new Try1_mid(this.t1, this.t2, this.t3, this.t4);
    }
}

class Try1_mid extends Try1_top {

    public Try1_mid(String t1, int t2, boolean t3, String t4) {
        super(this.t1, this.t2, this.t3, this.t4);
    }

    public Try1_mid combine(Try1 x) {
        return this;
    }

    public Try1_bot combine(Try1_bot x) {
        return new Try1_bot(this.t1, this.t2, this.t3, this.t4);
    }

    public Try1_mid combine(Try1_mid x) {
        return this;
    }
}

class Try1_bot extends Try1_mid {

    public Try1_bot(String t1, int t2, boolean t3, String t4) {
        super(this.t1, this.t2, this.t3, this.t4);
    }

    public Try1_bot combine(Try1 x) {
        return this;
    }

    public Try1_bot combine(Try1_bot x) {
        return this;
    }

    public Try1_bot combine(Try1_mid x) {
        return this;
    }
}

public class Application_Linear {

    public static void main(String[] args) {
        //top
        Try x = new Try("Top", 1, "afdasfa");
        //mid
        Try_mid m = new Try_mid("Middle");
        //bot
        Try_bot y = new Try_bot("Bot");
        y = x;
        x = m.combine(x.combine(c.combine(v.combine(b.combine(d)))));
        x = m + x + c + v + b + d;
        x = m.combine(y.combine(y));
        x = m.test1(y) + y;
        x = y.combine(m);
        x = y + m;
    }
}
