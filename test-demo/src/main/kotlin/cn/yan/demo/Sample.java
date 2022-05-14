package cn.yan.demo;

public class Sample {
    public void beforeInjected() {
        //logic
    }

    public void afterInjected() {
        long start = System.currentTimeMillis();
        //logic
        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println("[MTC] cost time:" + time);
    }
}
