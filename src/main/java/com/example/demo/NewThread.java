package com.example.demo;
public class NewThread implements Runnable {

    @Override
    public synchronized void run() {
        while(true){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("自定义线程运行了");
        }
    }

    public static void main(String[] args) {
        NewThread n = new NewThread();
        Thread thread = new Thread(n);//创建线程并且指定线程任务
        thread.start();//启动线程
        while(true){
            synchronized (n){
                System.out.println("主线程运行了");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                n.notifyAll();
            }
        }
    }
}