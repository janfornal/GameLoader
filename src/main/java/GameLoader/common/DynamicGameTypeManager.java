package GameLoader.common;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarFile;
import static GameLoader.common.Utility.uncheckURL;

/**
 * This class is thread-safe
 */
public class DynamicGameTypeManager extends StaticGameTypeManager {
    protected final ClassLoader loader;

    public DynamicGameTypeManager(String gamesPath) {
        List<JarFile> JARs = new ArrayList<>();
        searchForJars(new File(gamesPath), JARs);

        URL[] urls = JARs
                .stream()
                .map(file -> uncheckURL("jar:file:" + file.getName() + "!/", Service.GAME_TYPE_ERROR_STREAM))
                .filter(Objects::nonNull)
                .toArray(URL[]::new);

        loader = new URLClassLoader(urls);

        for (JarFile file : JARs)
            processJarFile(file);
    }

    protected void searchForJars(File file, List<JarFile> JARs) {
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            if (subFiles != null)
                for (File entry : subFiles)
                    searchForJars(entry, JARs);
        }

        if (file.isFile() && file.toString().endsWith(".jar"))
            try {
                JARs.add(new JarFile(file));
            } catch (IOException e) {
                e.printStackTrace(Service.GAME_TYPE_ERROR_STREAM);
            }
    }

    protected void processJarFile(JarFile jarFile) {
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

    @Override
    public ClassLoader getClassLoader() {
        return loader;
    }
}
