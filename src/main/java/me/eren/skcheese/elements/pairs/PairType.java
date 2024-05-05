package me.eren.skcheese.elements.pairs;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Pair;
import ch.njol.yggdrasil.Fields;

import java.io.StreamCorruptedException;

public class PairType {
    protected static void register() {
        Classes.registerClass(new ClassInfo<>(Pair.class, "pair")
                .user("pair")
                .name("Pair")
                .description("A pair is a single value that can hold 2 objects at once.")
                .since("1.4")
                .parser(new Parser<>() {
                    @Override
                    public boolean canParse(ParseContext parseContext) {
                        return false;
                    }

                    @Override
                    public String toString(Pair pair, int flags) {
                        return "pair:" + pair.getFirst() + ", " + pair.getSecond();
                    }

                    @Override
                    public String toVariableNameString(Pair pair) {
                        return "pair:.+";
                    }
                }).serializer(new Serializer<>() {
                    @Override
                    public Fields serialize(Pair pair) {
                        Fields fields = new Fields();
                        fields.putObject("first", pair.getFirst());
                        fields.putObject("second", pair.getSecond());
                        return fields;
                    }

                    @Override
                    public void deserialize(Pair o, Fields f) {
                    }

                    @Override
                    public Pair<?, ?> deserialize(Fields f) throws StreamCorruptedException {
                        Object first = f.getObject("first", Object.class);
                        Object second = f.getObject("second", Object.class);
                        return new Pair<>(first, second);
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
