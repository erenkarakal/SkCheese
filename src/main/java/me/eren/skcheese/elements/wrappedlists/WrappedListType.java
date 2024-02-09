package me.eren.skcheese.elements.wrappedlists;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.yggdrasil.Fields;
import me.eren.skcheese.SkCheese;

import java.io.StreamCorruptedException;
import java.util.TreeMap;

public class WrappedListType {
    static {
        if (SkCheese.isSyntaxEnabled("wrapped-lists"))
            Classes.registerClass(new ClassInfo<>(WrappedList.class, "wrappedlist")
                    .user("wrappedlist")
                    .name("Wrapped List")
                    .description("Represents a wrapped list.")
                    .since("1.3")
                    .parser(new Parser<>() {
                        @Override
                        public boolean canParse(ParseContext parseContext) {
                            return false;
                        }

                        @Override
                        public String toString(WrappedList wrappedList, int flags) {
                            return "wrappedlist:" + wrappedList.treeMap().toString();
                        }

                        @Override
                        public String toVariableNameString(WrappedList wrappedList) {
                            return "wrappedlist:.+";
                        }
                    }).serializer(new Serializer<>() {
                        @Override
                        public Fields serialize(WrappedList wrappedList) {
                            Fields fields = new Fields();
                            fields.putObject("treemap", wrappedList.treeMap());
                            return fields;
                        }

                        @Override
                        public void deserialize(WrappedList o, Fields f) {
                        }

                        @Override
                        public WrappedList deserialize(Fields f) throws StreamCorruptedException {
                            TreeMap<String, Object> treemap = f.getObject("treemap", TreeMap.class);
                            return new WrappedList(treemap);
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
