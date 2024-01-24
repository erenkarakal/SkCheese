package me.eren.skcheese.elements.task;

import java.util.concurrent.CompletableFuture;

public record Future(long timeout, CompletableFuture<Object> completableFuture) {

    public Future(long timeout) {
        this(timeout, new CompletableFuture<>());
    }

}
