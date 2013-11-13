package aes.utils;

import java.lang.reflect.Field;

public class PrivateFieldAccess {
	public static Field getField(Class<?> classType, String fieldName) {
		Field field;
		try {
			field = classType.getDeclaredField(fieldName);
		} catch (final NoSuchFieldException e) {
			final Class<?> superType = classType.getSuperclass();
			if (superType != null)
				return getField(superType, fieldName);
			e.printStackTrace();
			return null;
		} catch (final SecurityException e) {
			e.printStackTrace();
			return null;
		}
		field.setAccessible(true);
		return field;
	}

	public static Object getValue(Object object, String fieldName) {
		try {
			return getField(object.getClass(), fieldName).get(object);
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
