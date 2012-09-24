package org.onpu.websimlpe.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * This class is using for create instances of class by it default constructors
 */
public class ReflectiveBuilder {

    /**
     * This method is using for create instances of class by it default constructors
     * @param clazz Target class
     * @param <T> Target class
     * @return Instance of the target class
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static <T> T build(Class<?> clazz) throws IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Constructor[] constructors = clazz.getConstructors();

        if (constructors == null || constructors.length == 0) {
            throw new IllegalAccessException("Can't to get consttuctors array for " + clazz.getName());
        }

        for (Constructor constructor:constructors) {
            if (constructor.getParameterTypes().length == 0) {
                return (T) constructor.newInstance();
            }
        }

        throw new IllegalStateException("Can't find default constructor.");
    }
}
