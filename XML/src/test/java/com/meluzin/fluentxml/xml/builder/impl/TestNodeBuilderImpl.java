package com.meluzin.fluentxml.xml.builder.impl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.builder.XmlBuilderFactory;
import com.meluzin.fluentxml.xml.builder.XmlBuilderSAXFactory;
public class TestNodeBuilderImpl {
	@Test
	public void testPrintHelloWorld() {
		XmlBuilderFactory fac = getFactory();
		try (InputStream s = getClass().getResourceAsStream("/test.xml");
			 InputStream ss = getClass().getResourceAsStream("/test.xml")) {

			NodeBuilder n = fac.parseDocument(s);
			String source = convert(ss);
			//fac.renderNode(n, Paths.get("t:/test.xml"));
			//Assert.assertEquals(n.toString(), source);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private XmlBuilderFactory getFactory() {
		XmlBuilderFactory fac = new XmlBuilderSAXFactory();
		return fac;
	}
	@Test 
	public void testProcessingInstruction() {
		String renderNode = new XmlBuilderFactory().renderNode(new XmlBuilderFactory().createRootElement("x").addProcessingInstruction("a", "b"), false);
		assertEquals(renderNode, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><?a b?><x/>");
		String renderNode2 = new XmlBuilderFactory().renderNode(new XmlBuilderFactory().createRootElement("x").addProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"build-log.xsl\""), false);
		assertEquals(renderNode2, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><?xml-stylesheet type=\"text/xsl\" href=\"build-log.xsl\"?><x/>");
	}
	@Test
	public void test3PrintHelloWorld() {
		XmlBuilderFactory fac = getFactory();
		try (InputStream s = getClass().getResourceAsStream("/test3.xml");
			 InputStream ss = getClass().getResourceAsStream("/test3.xml")) {

			NodeBuilder n = fac.parseDocument(s);
			String source = convert(ss);
			//Assert.assertEquals(n.toString(), source);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	@Test
	public void test2PrintHelloWorld() {
		XmlBuilderFactory fac = getFactory();
		try (InputStream s = getClass().getResourceAsStream("/test2.xml");
			 InputStream ss = getClass().getResourceAsStream("/test2.xml")) {

			NodeBuilder n = fac.parseDocument(s);
			String source = convert(ss);
			//Assert.assertEquals(n.toString(), source);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private String convert(InputStream s) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = s.read(data, 0, data.length)) != -1) {
		  buffer.write(data, 0, nRead);
		}

		buffer.flush();

		return new String(buffer.toByteArray());
	}

	@Test
	public void testFindNodeWithSameLocation() {
		XmlBuilderFactory fac = new XmlBuilderFactory();
		NodeBuilder n = fac.createRootElement("x");
		NodeBuilder z = n.addChild("y").addChild("z");
		NodeBuilder n1 = fac.createRootElement("x");
		NodeBuilder z1 = n1.addChild("y").addChild("z");
		Optional<NodeBuilder> nb = n1.findNodeWithSameLocation(z);
		Assert.assertTrue(nb.isPresent());
		Assert.assertTrue(z1 == nb.get());		
	}
	
	@Test
	public void testFindNodeWithSameLocation2() {
		XmlBuilderFactory fac = new XmlBuilderFactory();
		NodeBuilder n = fac.createRootElement("x");
		NodeBuilder z = n.addChild("y").getParent().addChild("z");
		NodeBuilder n1 = fac.createRootElement("x");
		NodeBuilder z1 = n1.addChild("y").addChild("z");
		Optional<NodeBuilder> nb = n1.findNodeWithSameLocation(z);
		Assert.assertTrue(!nb.isPresent());	
	}

	@Test
	public void testFindNodeWithSameLocation3() {
		XmlBuilderFactory fac = new XmlBuilderFactory();
		NodeBuilder n = fac.createRootElement("x");
		NodeBuilder z = n.addChild("y").getParent().addChild("y").getParent().addChild("y").getParent().addChild("z");
		NodeBuilder n1 = fac.createRootElement("x");
		NodeBuilder z1 = n1.addChild("y").addChild("z");
		Optional<NodeBuilder> nb = n1.findNodeWithSameLocation(z);
		Assert.assertTrue(!nb.isPresent());	
	}
	@Test
	public void testFindNodeWithSameLocation4() {
		XmlBuilderFactory fac = new XmlBuilderFactory();
		NodeBuilder n = fac.createRootElement("x");
		NodeBuilder z = n.addChild("y").getParent().addChild("y").getParent().addChild("y").getParent().addChild("z");
		NodeBuilder n1 = fac.createRootElement("x");
		NodeBuilder z1 = n1.addChild("y").getParent().addChild("y").addChild("z");
		Optional<NodeBuilder> nb = n1.findNodeWithSameLocation(z);
		Assert.assertTrue(!nb.isPresent());	
	}
	@Test
	public void testFindNodeWithSameLocation5() {
		XmlBuilderFactory fac = new XmlBuilderFactory();
		NodeBuilder n = fac.createRootElement("x");
		NodeBuilder z = n.addChild("y").getParent().addChild("y").getParent().addChild("y").addChild("z");
		NodeBuilder n1 = fac.createRootElement("x");
		NodeBuilder z1 = n1.addChild("y").getParent().addChild("y").getParent().addChild("y").addChild("z");
		Optional<NodeBuilder> nb = n1.findNodeWithSameLocation(z);
		Assert.assertTrue(nb.isPresent());	
		Assert.assertTrue(z1 == nb.get());	
	}
	@Test
	public void testFindNodeWithSameLocation6() {
		XmlBuilderFactory fac = new XmlBuilderFactory();
		NodeBuilder n = fac.createRootElement("x");
		NodeBuilder z = n.addChild("y").getParent().addChild("b").getParent().addChild("y").addChild("z");
		NodeBuilder n1 = fac.createRootElement("x");
		NodeBuilder z1 = n1.addChild("y").getParent().addChild("c").getParent().addChild("y").addChild("z");
		z1.getParent().getParent().addChild("y");
		Optional<NodeBuilder> nb = n1.findNodeWithSameLocation(z);
		Assert.assertTrue(nb.isPresent());	
		Assert.assertTrue(z1 == nb.get());	
	}
	@Test
	public void testFindNodeWithSameLocation7() {
		XmlBuilderFactory fac = new XmlBuilderFactory();
		NodeBuilder n = fac.createRootElement("x");
		NodeBuilder z = n.addChild("a").getParent().addChild("b").getParent().addChild("y").addChild("z");
		NodeBuilder n1 = fac.createRootElement("x");
		NodeBuilder z1 = n1.addChild("a").getParent().addChild("c").getParent().addChild("y").addChild("z");
		z1.getParent().getParent().addChild("y");
		Optional<NodeBuilder> nb = n1.findNodeWithSameLocation(z);
		Assert.assertTrue(nb.isPresent());	
		Assert.assertTrue(z1 == nb.get());	
	}
	@Test
	public void testEquals() {
		NodeBuilder n1 = getFactory().createRootElement("test");
		NodeBuilder n2 = getFactory().createRootElement("test");
		
		Assert.assertTrue(!n1.equals(n2));		
		Assert.assertTrue(n1.equalsTo(n2));
	}
	@Test
	public void testEquals2() {
		NodeBuilder n1 = getFactory().createRootElement("test");
		n1.addChild("x").addChild("y");
		NodeBuilder n2 = getFactory().createRootElement("test");
		n2.addChild("x").addChild("y");
		
		Assert.assertTrue(!n1.equals(n2));		
		Assert.assertTrue(n1.equalsTo(n2));
	}
	@Test
	public void testEquals3() {
		NodeBuilder n1 = getFactory().createRootElement("test");
		n1.addChild("x").getParent().addChild("y");
		NodeBuilder n2 = getFactory().createRootElement("test");
		n2.addChild("x").addChild("y");
		
		Assert.assertTrue(!n1.equals(n2));		
		Assert.assertTrue(!n1.equalsTo(n2));
	}
	@Test
	public void testEquals4() {
		NodeBuilder n1 = getFactory().createRootElement("test");
		n1.addChild("x").addChild("y").addAttribute("y", "z");
		NodeBuilder n2 = getFactory().createRootElement("test");
		n2.addChild("x").addChild("y");
		
		Assert.assertTrue(!n1.equals(n2));		
		Assert.assertTrue(!n1.equalsTo(n2));
	}
	@Test
	public void testEquals5() {
		NodeBuilder n1 = getFactory().createRootElement("test");
		n1.addChild("x").addChild("y").addAttribute("y", "z");
		NodeBuilder n2 = getFactory().createRootElement("test");
		n2.addChild("x").addChild("y");
		
		Assert.assertTrue(!n1.equals(n2));		
		Assert.assertTrue(!n1.equalsTo(n2));	
		Optional<String> firstDiff = n1.getFirstDiff(n2);
		Assert.assertTrue(firstDiff.isPresent());
		Assert.assertTrue(firstDiff + " " + "/test[1]/x[1]/y[1] - node has here different attributes: [y=z] != []", firstDiff.equals(Optional.of("/test[1]/x[1]/y[1] - node has here different attributes: [y=z] != []")));
	}

	@Test 
	public void testAttributesDiff() {
		NodeBuilder n1 = getFactory().createRootElement("x").addAttribute("a", "b");
		NodeBuilder n2 = getFactory().createRootElement("x").addAttribute("a", "b");

		Assert.assertTrue(!n1.equals(n2));		
		Assert.assertTrue(n1.equalsTo(n2));	
		Optional<String> firstDiff = n1.getFirstDiff(n2);
		Assert.assertTrue(!firstDiff.isPresent());
		
	}
	@Test 
	public void testAttributesDiff2() {
		NodeBuilder n1 = getFactory().createRootElement("x").addAttribute("a", "b");
		NodeBuilder n2 = getFactory().createRootElement("x").addAttribute("c", "b");

		Assert.assertTrue(!n1.equals(n2));		
		Assert.assertTrue(!n1.equalsTo(n2));	
		Optional<String> firstDiff = n1.getFirstDiff(n2);
		Assert.assertTrue(firstDiff.isPresent());
		
	}
	@Test 
	public void testXmlLangAttribute() {
		XmlBuilderFactory fac = new XmlBuilderFactory();
		NodeBuilder element = fac.createRootElement("element").addAttribute("xml","lang", "en");
		assertThat(element, not(is(nullValue())));
		String string = element.toString();
		assertThat(string, containsString("xml:lang=\"en\""));
	}
}
