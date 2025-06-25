package kr.kro.dslofficial.test;

public class Test {
    // method
    // [접근 제한자] [동적여부(static, non-static)] [반환타입] [함수이름] ([매개변수]) {}
    // public or protected or private
    public static void main(String[] args) {
        System.out.println(f(2, 5, 10));
    }

    public static int f(int x, int y, int z) {
        return x * y * z;
    }
}