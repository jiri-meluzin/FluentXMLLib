package com.meluzin.functional;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private String name;
	private Node parent;
	private List<Node> nodes = new ArrayList<Node>();
	public List<Node> getNodes() {
		return nodes;
	}
	public String getName() {
		return name;
	}
	public Node add(String name) {
		Node n = new Node();
		n.name = name;
		n.parent = this;
		nodes.add(n);
		return n;
	}
	public Node getParent() {
		return parent;
	}
	
}