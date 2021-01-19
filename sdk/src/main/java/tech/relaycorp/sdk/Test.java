package tech.relaycorp.sdk;

import org.jetbrains.annotations.NotNull;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;

class Test {
    public static void main(String[] args) {
        RelaynetClient.INSTANCE.getGateway().bind(new Continuation<Unit>() {
            @NotNull
            @Override
            public CoroutineContext getContext() {
                return null;
            }

            @Override
            public void resumeWith(@NotNull Object o) {

            }
        });
    }
}
