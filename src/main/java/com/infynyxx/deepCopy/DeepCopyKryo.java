package com.infynyxx.deepCopy;

import com.esotericsoftware.kryo.Kryo;

public class DeepCopyKryo {

    public static Object copy(Object orig) {
        Kryo KRYO = new Kryo();
        return KRYO.copy(orig);
    }
}
