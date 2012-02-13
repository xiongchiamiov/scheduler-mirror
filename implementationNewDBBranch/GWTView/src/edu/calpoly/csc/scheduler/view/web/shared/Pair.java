package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;

public class Pair<A, B> implements Serializable {
	A a;
	B b;
	Pair() { }
	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
	public A getLeft() { return a; }
	public B getRight() { return b; }
	
//	public static <A, B> Pair<A, B> create(A a, B b) {
//		return new Pair<A, B>(a, b);
//	}
}
