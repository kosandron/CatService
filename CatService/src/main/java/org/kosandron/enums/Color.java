package org.kosandron.enums;

import java.util.HashSet;
import java.util.Set;

public enum Color {
    ORANGE,
    YELLOW,
    BLUE,
    BROWN,
    LILAC,
    WHITE,
    GRAY,
    BLACK;

    private final static Set<String> values = new HashSet<String>(Color.values().length);
    static{
        for(Color f: Color.values())
            values.add(f.name());
    }

    public static boolean contains( String value ){
        return values.contains(value);
    }
}
