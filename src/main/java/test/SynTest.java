package test;

public class SynTest {
    public static void main(String[] args) {
        Object lock = new Object();

        new Thread(()->{
            while (true) {
                synchronized (lock) {
                    System.out.println("thread1 get lock");

                    System.out.println("thread1 release lock");
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(()->{
            while (true) {
                synchronized (lock) {
                    System.out.println("thread2 get lock");
                    System.out.println("thread2 release lock");
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        while (true){

        }
    }
}
