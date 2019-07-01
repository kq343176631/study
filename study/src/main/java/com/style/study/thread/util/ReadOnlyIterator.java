package com.style.study.thread.util;

import java.util.Iterator;

public class ReadOnlyIterator<E> implements Iterator<E> {
	private final Iterator<E> delegate;

	private ReadOnlyIterator(Iterator<E> delegate) {
		this.delegate = delegate;
	}

	public static <E> Iterator<E> with(Iterator<E> delegate) {
		return new ReadOnlyIterator<E>(delegate);
	}

	@Override
	public boolean hasNext() {
		return delegate.hasNext();
	}

	@Override
	public E next() {
		return delegate.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove");
	}
}