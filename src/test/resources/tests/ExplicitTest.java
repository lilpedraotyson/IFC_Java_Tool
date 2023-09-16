class Test {
    public String t;

    public Test(String t) {this.t = t;}

    public void method1(Test x) {}

    public Test method2(Test x) {
        return x;
    }

    public Test method3(Test x, Test y) {
        return x;
    }
}

public class Application {
    public static void main(String[] args) {
    }
}