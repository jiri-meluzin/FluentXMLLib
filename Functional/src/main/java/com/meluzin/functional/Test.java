package com.meluzin.functional;

import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Test {
	public static final Logger l = Log.get();
	public static void main(String[] args) {
		l.setLevel(Level.FINEST);
		Node n = new Node();
		n.add("s").add("b").add("c");
		BaseRecursiveIterator<Node> b = new BaseRecursiveIterator<Node>(new ChildrenAccessor<Node>() {

			@Override
			public Iterable<Node> getChildren(Node currentItem) {
				l.finest("getting children");
				return currentItem.getNodes();
			}
		}, new Predicate<Node>() {

			@Override
			public boolean test(Node t) {
				l.fine("exec " + t.getName());
				return "x".equals(t.getName());
			}
		}, true, n);
		b.next();
		Log.get();
	}
}
