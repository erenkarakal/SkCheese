package me.eren.skcheese.elements.task;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.yggdrasil.Fields;

import java.io.StreamCorruptedException;

public class FutureType {
    static {
        Classes.registerClass(new ClassInfo<>(Future.class, "future")
                .user("future")
                .name("Future")
                .description("Represents a completable future.")
                .since("1.2")
                .parser(new Parser<>() {
                    @Override
                    public boolean canParse(ParseContext parseContext) {
                        return false;
                    }

                    @Override
                    public String toString(Future future, int flags) {
                        return "future:" + future.timeout();
                    }

                    @Override
                    public String toVariableNameString(Future future) {
                        return "future:.+";
                    }
                }).serializer(new Serializer<>() {
                    @Override
                    public Fields serialize(Future future) {
                        Fields fields = new Fields();
                        fields.putObject("timeout", future.timeout());
                        return fields;
                    }

                    @Override
                    public void deserialize(Future o, Fields f) {
                    }

                    @Override
                    public Future deserialize(Fields f) throws StreamCorruptedException {
                        long timeout = f.getObject("timeout", Long.class);
                        return new Future(timeout);
                    }

                    @Override
                    public boolean mustSyncDeserialization() {
                        return false;
                    }

                    @Override
                    protected boolean canBeInstantiated() {
                        return false;
                    }
                })
        );
    }
}
