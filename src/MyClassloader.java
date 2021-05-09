import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyClassloader extends ClassLoader {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final String className = "Hello";
        final String methodName = "hello";
        ClassLoader classLoader = new MyClassloader();
        Class<?> clazz = classLoader.loadClass(className);

        Object instance=clazz.getDeclaredConstructor().newInstance();
        Method method=clazz.getMethod(methodName);
        method.invoke(instance);
    }

    //获取类文件信息
    @Override
    protected Class<?> findClass(String classname) throws ClassNotFoundException {

        String filePath = classname.replace(".", "/");
        final String suffix = ".xlass";
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filePath + suffix);
        try {
            int length = inputStream.available();
            byte[] byteArray = new byte[length];
            inputStream.read(byteArray);

            byte[] classBytes = decode(byteArray);
            return defineClass(classname, classBytes, 0, classBytes.length);
        } catch (IOException ex) {
            throw new ClassNotFoundException(classname, ex);
        } finally {
            close(inputStream);
        }
    }

    //解码类文件
    private static byte[] decode(byte[] byteArray) {
        byte[] targetArray = new byte[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            targetArray[i] = (byte) (255 - byteArray[i]);
        }
        return targetArray;
    }

    //关闭IO流
    private static void close(Closeable res) {
        if (null != res) {
            try {
                res.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
