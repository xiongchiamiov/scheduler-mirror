package scheduler.view.web.client.views.resources;

import java.util.Iterator;

import scheduler.view.web.shared.Pair;

class DoubleIterator<A, B> implements Iterable<Pair<A, B>>, Iterator<Pair<A, B>> {
	Iterator<A> iterA;
	Iterator<B> iterB;

	DoubleIterator(Iterable<A> iterableA, Iterable<B> iterableB) {
		this(iterableA.iterator(), iterableB.iterator());
	}
	
	DoubleIterator(Iterator<A> iterA, Iterator<B> iterB) {
		this.iterA = iterA;
		this.iterB = iterB;
	}
	
	@Override
	public Iterator<Pair<A, B>> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		assert iterA.hasNext() == iterB.hasNext();
		return iterA.hasNext();
	}

	@Override
	public Pair<A, B> next() {
		assert(hasNext());
		return new Pair<A, B>(iterA.next(), iterB.next());
	}

	@Override
	public void remove() {
		assert(hasNext());
		iterA.remove();
		iterB.remove();
	}
}