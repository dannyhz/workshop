package cn.evun.sweet.common.util;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * 对象操作工具类
 *
 * @author yangw
 * @since 1.0.0
 */
public abstract class ObjectUtils {

	private static final int INITIAL_HASH = 7;
	private static final int MULTIPLIER = 31;
	
	private static final String EMPTY_STRING = "";
	private static final String NULL_STRING = "null";
	private static final String ARRAY_START = "{";
	private static final String ARRAY_END = "}";
	private static final String EMPTY_ARRAY = ARRAY_START + ARRAY_END;
	private static final String ARRAY_ELEMENT_SEPARATOR = ", ";

	/**
	 * 判断是否是自定义的java bean对象类型。
	 */
	public static boolean isJavaBean(Class<?> cls) {
		if (cls.isPrimitive()
				||(cls == Integer.class)
				|| (cls == Double.class) 
				|| (cls == Boolean.class) 
				|| (cls == Byte.class)
				|| (cls == Character.class) 
				|| (cls == Short.class)
				|| (cls == Long.class)
				|| (cls == Float.class) 
				|| (cls == BigInteger.class) 
				|| (cls == BigDecimal.class)
				|| (cls == String.class)) {
			return false;
		}
		if (Map.class.isAssignableFrom(cls)
				||Date.class.isAssignableFrom(cls)
				||Calendar.class.isAssignableFrom(cls)
				||Collection.class.isAssignableFrom(cls)
				||Array.class.isAssignableFrom(cls)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 判断是否是checked型异常，即不是运行时异常，也不是Error。
	 */
	public static boolean isCheckedException(Throwable ex) {
		return !(ex instanceof RuntimeException || ex instanceof Error);
	}

	/**
	 * 判断给定的异常是不是声明的异常链中的某一个异常或其子类
	 * @param ex the exception to check
	 * @param declaredExceptions the exception types declared in the throws clause
	 * @return whether the given exception is compatible
	 */
	public static boolean isCompatibleWithThrowsClause(Throwable ex, Class<?>... declaredExceptions) {
		if (!isCheckedException(ex)) {
			return true;
		}
		if (declaredExceptions != null) {
			for (Class<?> declaredException : declaredExceptions) {
				if (declaredException.isInstance(ex)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断对象是否是数组对象
	 */
	public static boolean isArray(Object obj) {
		return (obj != null && obj.getClass().isArray());
	}

	/**
	 * 判断数组是否为空或null
	 */
	public static boolean isEmpty(Object[] array) {
		return (array == null || array.length == 0);
	}

	/**
	 * 数组中是否包含指定的元素
	 */
	public static boolean containsElement(Object[] array, Object element) {
		if (array == null) {
			return false;
		}
		for (Object arrayEle : array) {
			if (nullSafeEquals(arrayEle, element)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查枚举类型的数组是否包含指定名称的常量（元素），忽略大小写敏感。
	 */
	public static boolean containsConstant(Enum<?>[] enumValues, String constant) {
		return containsConstant(enumValues, constant, false);
	}

	/**
	 * 检查枚举类型的数组是否包含指定名称的常量（元素），需指定大小写是否敏感。
	 */
	public static boolean containsConstant(Enum<?>[] enumValues, String constant, boolean caseSensitive) {
		for (Enum<?> candidate : enumValues) {
			if (caseSensitive ?
					candidate.toString().equals(constant) :
					candidate.toString().equalsIgnoreCase(constant)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将指定的对象添加到指定的数组中，并返回一个新的数组
	 */
	public static <A, O extends A> A[] addObjectToArray(A[] array, O obj) {
		Class<?> compType = Object.class;
		if (array != null) {
			compType = array.getClass().getComponentType();
		}
		else if (obj != null) {
			compType = obj.getClass();
		}
		int newArrLength = (array != null ? array.length + 1 : 1);
		@SuppressWarnings("unchecked")
		A[] newArr = (A[]) Array.newInstance(compType, newArrLength);
		if (array != null) {
			System.arraycopy(array, 0, newArr, 0, array.length);
		}
		newArr[newArr.length - 1] = obj;
		return newArr;
	}

	/**
	 * 将一个可能的原生数组对象解析成一个Object[]数组。null将被解析成空数组
	 */
	public static Object[] toObjectArray(Object source) {
		if (source instanceof Object[]) {
			return (Object[]) source;
		}
		if (source == null) {
			return new Object[0];
		}
		if (!source.getClass().isArray()) {
			throw new IllegalArgumentException("Source is not an array: " + source);
		}
		int length = Array.getLength(source);
		if (length == 0) {
			return new Object[0];
		}
		Class<?> wrapperType = Array.get(source, 0).getClass();
		Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
		for (int i = 0; i < length; i++) {
			newArray[i] = Array.get(source, i);
		}
		return newArray;
	}


	//---------------------------------------------------------------------
	// Convenience methods for content-based equality/hash-code handling
	//---------------------------------------------------------------------

	/**
	 *  支持null值的对象比较方法。
	 * <p>基于 {@code Arrays.equals}, 对数组进行比较.
	 */
	public static boolean nullSafeEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 == null || o2 == null) {
			return false;
		}
		if (o1.equals(o2)) {
			return true;
		}
		if (o1.getClass().isArray() && o2.getClass().isArray()) {
			if (o1 instanceof Object[] && o2 instanceof Object[]) {
				return Arrays.equals((Object[]) o1, (Object[]) o2);
			}
			if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
				return Arrays.equals((boolean[]) o1, (boolean[]) o2);
			}
			if (o1 instanceof byte[] && o2 instanceof byte[]) {
				return Arrays.equals((byte[]) o1, (byte[]) o2);
			}
			if (o1 instanceof char[] && o2 instanceof char[]) {
				return Arrays.equals((char[]) o1, (char[]) o2);
			}
			if (o1 instanceof double[] && o2 instanceof double[]) {
				return Arrays.equals((double[]) o1, (double[]) o2);
			}
			if (o1 instanceof float[] && o2 instanceof float[]) {
				return Arrays.equals((float[]) o1, (float[]) o2);
			}
			if (o1 instanceof int[] && o2 instanceof int[]) {
				return Arrays.equals((int[]) o1, (int[]) o2);
			}
			if (o1 instanceof long[] && o2 instanceof long[]) {
				return Arrays.equals((long[]) o1, (long[]) o2);
			}
			if (o1 instanceof short[] && o2 instanceof short[]) {
				return Arrays.equals((short[]) o1, (short[]) o2);
			}
		}
		return false;
	}
	
	/**
	 * 支持各种类型数组的hashcode算法，支持null。
	 */
	public static int nullSafeHashCode(Object obj) {
		if (obj == null) {
			return 0;
		}
		if (obj.getClass().isArray()) {
			if (obj instanceof Object[]) {
				return nullSafeHashCode((Object[]) obj);
			}
			if (obj instanceof boolean[]) {
				return nullSafeHashCode((boolean[]) obj);
			}
			if (obj instanceof byte[]) {
				return nullSafeHashCode((byte[]) obj);
			}
			if (obj instanceof char[]) {
				return nullSafeHashCode((char[]) obj);
			}
			if (obj instanceof double[]) {
				return nullSafeHashCode((double[]) obj);
			}
			if (obj instanceof float[]) {
				return nullSafeHashCode((float[]) obj);
			}
			if (obj instanceof int[]) {
				return nullSafeHashCode((int[]) obj);
			}
			if (obj instanceof long[]) {
				return nullSafeHashCode((long[]) obj);
			}
			if (obj instanceof short[]) {
				return nullSafeHashCode((short[]) obj);
			}
		}
		return obj.hashCode();
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * If {@code array} is {@code null}, this method returns 0.
	 */
	public static int nullSafeHashCode(Object[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		for (Object element : array) {
			hash = MULTIPLIER * hash + nullSafeHashCode(element);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * If {@code array} is {@code null}, this method returns 0.
	 */
	public static int nullSafeHashCode(boolean[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		for (boolean element : array) {
			hash = MULTIPLIER * hash + hashCode(element);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * If {@code array} is {@code null}, this method returns 0.
	 */
	public static int nullSafeHashCode(byte[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		for (byte element : array) {
			hash = MULTIPLIER * hash + element;
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * If {@code array} is {@code null}, this method returns 0.
	 */
	public static int nullSafeHashCode(char[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		for (char element : array) {
			hash = MULTIPLIER * hash + element;
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * If {@code array} is {@code null}, this method returns 0.
	 */
	public static int nullSafeHashCode(double[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		for (double element : array) {
			hash = MULTIPLIER * hash + hashCode(element);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * If {@code array} is {@code null}, this method returns 0.
	 */
	public static int nullSafeHashCode(float[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		for (float element : array) {
			hash = MULTIPLIER * hash + hashCode(element);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * If {@code array} is {@code null}, this method returns 0.
	 */
	public static int nullSafeHashCode(int[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		for (int element : array) {
			hash = MULTIPLIER * hash + element;
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * If {@code array} is {@code null}, this method returns 0.
	 */
	public static int nullSafeHashCode(long[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		for (long element : array) {
			hash = MULTIPLIER * hash + hashCode(element);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * If {@code array} is {@code null}, this method returns 0.
	 */
	public static int nullSafeHashCode(short[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		for (short element : array) {
			hash = MULTIPLIER * hash + element;
		}
		return hash;
	}

	/**
	 * Return the same value as {@link Boolean#hashCode()}}.
	 * @see Boolean#hashCode()
	 */
	public static int hashCode(boolean bool) {
		return (bool ? 1231 : 1237);
	}

	/**
	 * Return the same value as {@link Double#hashCode()}}.
	 * @see Double#hashCode()
	 */
	public static int hashCode(double dbl) {
		return hashCode(Double.doubleToLongBits(dbl));
	}

	/**
	 * Return the same value as {@link Float#hashCode()}}.
	 * @see Float#hashCode()
	 */
	public static int hashCode(float flt) {
		return Float.floatToIntBits(flt);
	}

	/**
	 * Return the same value as {@link Long#hashCode()}}.
	 * @see Long#hashCode()
	 */
	public static int hashCode(long lng) {
		return (int) (lng ^ (lng >>> 32));
	}

	//---------------------------------------------------------------------
	// Convenience methods for toString output
	//---------------------------------------------------------------------

	/**
	 * 用字符串的形式描述obj的内容，支持各种类型的数组（）。<br/>
	 * 展现结果形如："{1,2,3}"。<br/>
	 * 注意：null值将返回""
	 */
	public static String getDisplayString(Object obj) {
		if (obj == null) {
			return EMPTY_STRING;
		}
		return nullSafeToString(obj);
	}

	/**
	 * 获取对象的类名，兼容null值
	 */
	public static String nullSafeClassName(Object obj) {
		return (obj != null ? obj.getClass().getName() : NULL_STRING);
	}

	/**
	 * 用字符串的形式描述obj的内容，支持各种类型的数组（）。<br/>
	 * 展现结果形如："{1,2,3}"。<br/>
	 * 注意：null值将返回"null"
	 */
	public static String nullSafeToString(Object obj) {
		if (obj == null) {
			return NULL_STRING;
		}
		if (obj instanceof String) {
			return (String) obj;
		}
		if (obj instanceof Object[]) {
			return nullSafeToString((Object[]) obj);
		}
		if (obj instanceof boolean[]) {
			return nullSafeToString((boolean[]) obj);
		}
		if (obj instanceof byte[]) {
			return nullSafeToString((byte[]) obj);
		}
		if (obj instanceof char[]) {
			return nullSafeToString((char[]) obj);
		}
		if (obj instanceof double[]) {
			return nullSafeToString((double[]) obj);
		}
		if (obj instanceof float[]) {
			return nullSafeToString((float[]) obj);
		}
		if (obj instanceof int[]) {
			return nullSafeToString((int[]) obj);
		}
		if (obj instanceof long[]) {
			return nullSafeToString((long[]) obj);
		}
		if (obj instanceof short[]) {
			return nullSafeToString((short[]) obj);
		}
		String str = obj.toString();
		return (str != null ? str : EMPTY_STRING);
	}

	
	public static String nullSafeToString(Object[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(String.valueOf(array[i]));
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	public static String nullSafeToString(boolean[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}

			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	public static String nullSafeToString(byte[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	public static String nullSafeToString(char[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append("'").append(array[i]).append("'");
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	public static String nullSafeToString(double[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}

			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	public static String nullSafeToString(float[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}

			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	public static String nullSafeToString(int[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	public static String nullSafeToString(long[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	public static String nullSafeToString(short[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

}
