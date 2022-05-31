package GameLoader.common;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public interface Utility {
    /**
     * @throws RuntimeException if {@code val} is {@code false}
     */
    static void runtimeAssert(boolean val) {
        if (!val)
            throw new RuntimeException();
    }

    /**
     * @throws RuntimeException if {@code val} is {@code false}
     */
    static void runtimeAssert(boolean val, String exc) {
        if (!val)
            throw new RuntimeException(exc);
    }

    record IntPair(int first, int second) {
        public int x() {
            return first;
        }
        public int y() {
            return second;
        }
    }

    record Pair<U, V>(U first, V second) {
        public U x() {
            return first;
        }
        public V y() {
            return second;
        }
    }

    static <E> E callDef(Callable<E> callable, E def, PrintStream err) {
        try {
            return callable.call();
        } catch (Exception e) {
            if (err != null)
                e.printStackTrace(err);
            return def;
        }
    }
    static <E> E callDef(Callable<E> callable, E def) {
        return callDef(callable, def, null);
    }

    static URL uncheckURL(String url, PrintStream err) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            if (err != null)
                e.printStackTrace(err);
            return null;
        }
    }

    static URL uncheckURL(String url) {
        return uncheckURL(url, null);
    }

    class ObjectInputStreamWithClassLoader extends ObjectInputStream {
        protected ClassLoader loader;
        public ObjectInputStreamWithClassLoader(InputStream in, ClassLoader classLoader) throws IOException {
            super(in);
            loader = classLoader;
        }

        // this is copied directly from Java source code
        private static final Map<String, Class<?>> primClassesCopy = Map.of(
                "boolean", boolean.class,
                "byte", byte.class,
                "char", char.class,
                "short", short.class,
                "int", int.class,
                "long", long.class,
                "float", float.class,
                "double", double.class,
                "void", void.class
        );

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc)
                throws IOException, ClassNotFoundException
        {
            String name = desc.getName();
            try {
                return Class.forName(name, false, loader);
            } catch (ClassNotFoundException ex) {
                Class<?> cl = primClassesCopy.get(name);
                if (cl != null) {
                    return cl;
                } else {
                    throw ex;
                }
            }
        }
    }

}
