package com.meluzin.functional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

public class Lists {
	@SafeVarargs
	@SuppressWarnings("hiding")
	public static <T> List<T> asList(T... items) {
		List<T> l = new ArrayList<T>();
		for (int i = 0; i < items.length; i++)
			l.add(items[i]);
		return l;
	}
	public static String join(Iterable<String> strings, String separator) {
		return join(strings, separator, false);
	}
	public static String join(Iterable<String> strings, String separator, boolean removeNulls) {
		return join(StreamSupport.stream(strings.spliterator(), false).filter(s -> !removeNulls || s != null).toArray(), separator);
	}
	public static String join(Object[] strings, String separator) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < strings.length; i++) {
			if (i != 0) {
				result.append(separator);
			}
			result.append(strings[i]);
		}
		return result.toString();
	}
	public static <S, R> Iterable<R> map(final Iterable<S> items, final Function<S, R> mapper) {
		return new Iterable<R>() {
			@Override
			public Iterator<R> iterator() {
				final Iterator<S> it = items.iterator();
				return new Iterator<R>() {

					@Override
					public boolean hasNext() {
						return it.hasNext();
					}

					@Override
					public R next() {
						return mapper.exec(it.next());
					}

					@Override
					public void remove() {
						it.remove();
					}
				};
			}
		};
	}
}
