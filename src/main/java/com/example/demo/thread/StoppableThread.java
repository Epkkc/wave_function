package com.example.demo.thread;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoppableThread extends Thread {

    private boolean stopped = false;

    public StoppableThread(Runnable runnable) {
        super(runnable);
    }

    public void changeStopped() {
        stopped = !stopped;
    }


}
