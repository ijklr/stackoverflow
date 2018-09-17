package com.mycompany.app;

import java.util.concurrent.CompletableFuture;

public class AsyncTimeout {

    String func1() {
        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
                System.out.println("after sleeping, func1 is done");
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("sleep messed up in func1");
            }
            return "from async in func1";
        });
        return "I am func1";
    }

    String func2() {
        try {
            Thread.sleep(2005);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("sleep messed up in func2");
        }
        return "I am func2";
    }

    public static void main(String args[]) {
        System.out.println("hello");
        AsyncTimeout asyncTimeout = new AsyncTimeout();
        System.out.println(asyncTimeout.func1());
        System.out.println(asyncTimeout.func2());
    }
}
