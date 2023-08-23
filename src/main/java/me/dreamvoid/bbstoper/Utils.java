package me.dreamvoid.bbstoper;

public class Utils {
    public static boolean findClass(String className){
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e){
            return false;
        }
    }
}
