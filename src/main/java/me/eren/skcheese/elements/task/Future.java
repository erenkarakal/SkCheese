package me.eren.skcheese.elements.task;

import java.util.concurrent.CompletableFuture;

public class Future {

    public long timeout;
    public CompletableFuture<Object> completableFuture;

    public Future(long timeout) {
        this.timeout = timeout;
        this.completableFuture = new CompletableFuture<>();
    }
}
