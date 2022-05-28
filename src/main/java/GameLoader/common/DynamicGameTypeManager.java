package GameLoader.common;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

/**
 * This class is not thread-safe
 */
public class DynamicGameTypeManager extends StaticGameTypeManager {
    public DynamicGameTypeManager(String gamesPath) {
        processFile(new File(gamesPath));
    }

    protected void processFile(File file) {
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            if (subFiles != null)
                for (File entry : subFiles)
                    processFile(entry);
        }

        if (file.isFile() && file.toString().endsWith(".jar"))
            try {
                processJarFile(new JarFile(file));
            } catch (IOException e) {
                e.printStackTrace(Service.GAME_TYPE_ERROR_STREAM);
            }
    }

    protected void processJarFile(JarFile jarFile) throws IOException {
        URL[] urls = { new URL("jar:file:" + jarFile.getName() + "!/") };
        URLClassLoader loader = URLClassLoader.newInstance(urls);

        jarFile.stream().forEach(je -> {
            String fileName = je.getName();
            if (je.isDirectory() || !fileName.endsWith(".class"))
                return;

            String className = fileName
                    .substring(0, fileName.length() - 6)
                    .replace('/', '.');

            try {
                Class<?> cl = loader.loadClass(className);

                int mod = cl.getModifiers();
                if (Modifier.isInterface(mod) || Modifier.isAbstract(mod))
                    return;

                registerGameClass(cl.asSubclass(Game.class));

            } catch (ClassNotFoundException | NoClassDefFoundError | ClassCastException ignored) {}
        });
    }
}
