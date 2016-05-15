package com.ceabie.util;

import java.io.*;

/**
 * The type File util.
 *
 * @author chenxi
 */
public class IOUtil {
    public static boolean saveObjectToFile(File file, Object object) {
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            os.writeObject(object);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            safeClose(os);
        }

        return false;
    }

    public static <T> T importObjectFromFile(File file, Class<T> cls) {
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            return cls.cast(is.readObject());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            safeClose(is);
        }

        return null;
    }

    public static void safeClose(Closeable os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
