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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import com.meluzin.fluentxml.xml.builder.NodeBuilder;
import com.meluzin.fluentxml.xml.builder.XmlBuilderFactory;
import com.meluzin.fluentxml.xml.builder.XmlBuilderSAXFactory;
import com.meluzin.fluentxml.xml.builder.XmlBuilderSAXFactory.Settings;
import com.meluzin.functional.FileSearcher;
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
		assertEquals(renderNode, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><?a b?><x/>");
		String renderNode2 = new XmlBuilderFactory().renderNode(new XmlBuilderFactory().createRootElement("x").addProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"build-log.xsl\""), false);
		assertEquals(renderNode2, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><?xml-stylesheet type=\"text/xsl\" href=\"build-log.xsl\"?><x/>");
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

		n1.addAttribute("a", (String)null);
		n2.addAttribute("a", (String)null);
		Assert.assertTrue(!n1.equals(n2));
		Assert.assertTrue(n1.equalsTo(n2));
		Assert.assertTrue(!n1.getFirstDiff(n2).isPresent());
		
	}
	@Test 
	public void testAttributesDiff2() {
		NodeBuilder n1 = getFactory().createRootElement("x").addAttribute("a", "b");
		NodeBuilder n2 = getFactory().createRootElement("x").addAttribute("c", "b");

		Assert.assertTrue(!n1.equals(n2));
		Assert.assertTrue(!n1.equalsTo(n2));
		Optional<String> firstDiff = n1.getFirstDiff(n2);
		Assert.assertTrue(firstDiff.isPresent());

		n1.addAttribute("c", "b");
		Assert.assertTrue(!n1.equals(n2));
		Assert.assertTrue(!n1.equalsTo(n2));
		Assert.assertTrue(n1.getFirstDiff(n2).isPresent());
		
		n1.addAttribute("a", (String)null);
		n2.addAttribute("a", "a");
		Assert.assertTrue(!n1.equals(n2));
		Assert.assertTrue(!n1.equalsTo(n2));
		Assert.assertTrue(n1.getFirstDiff(n2).isPresent());
		
	}
	@Test 
	public void testXmlLangAttribute() {
		XmlBuilderFactory fac = new XmlBuilderFactory();
		NodeBuilder element = fac.createRootElement("element").addAttribute("xml","lang", "en");
		assertThat(element, not(is(nullValue())));
		String string = element.toString();
		assertThat(string, containsString("xml:lang=\"en\""));
	}
	@Test
	public void testEmptyText() {
		XmlBuilderFactory fac = new XmlBuilderFactory();
		NodeBuilder element = fac.createRootElement("element").addAttribute("xml","lang", "en").addChild("x").setTextContent(null).getRoot();
		NodeBuilder element2 = fac.createRootElement("element").addAttribute("xml","lang", "en").addChild("x").setTextContent("").getRoot();
		assertThat(element2.getFirstDiff(element), is(Optional.empty()));
		assertThat(element.getFirstDiff(element2), is(Optional.empty()));
		assertThat(element2.equalsTo(element), is(true));
	}

	@Test
	public void testAttributeStability() {
		XmlBuilderSAXFactory xmlBuilderSAXFactory = new XmlBuilderSAXFactory();
		String sourceXml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ "<xsd:element xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" name=\"ErrorRows\" minOccurs=\"0\" />\r\n"
				+ "";
		NodeBuilder node = xmlBuilderSAXFactory.parseDocument(sourceXml);
		assertEquals(sourceXml, node.toString());
	}
	@Test
	public void testAttributeStability2() {
		XmlBuilderSAXFactory xmlBuilderSAXFactory = new XmlBuilderSAXFactory();
		String sourceXml=Settings.BOM_CHAR+"<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
				+ "<xsd:element name=\"ErrorRows\" minOccurs=\"&#xA;&gt;0\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/>\r\n"
				+ "";
		NodeBuilder node = xmlBuilderSAXFactory.parseDocument(sourceXml);
		assertEquals(sourceXml, node.toString());
	}
	@Test
	public void testAttributeStability3() {
		XmlBuilderSAXFactory xmlBuilderSAXFactory = new XmlBuilderSAXFactory();
		String sourceXml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ "<pd:label xmlns:pd=\"a>\">\r\n"
				+ "    <!-- comment\r\n"
				+ "     -->\r\n"
				+ "    <pd:description>Workaround value:\r\n"
				+ "4, Customer = V1, subscriber = V1, user = V1"
				+ "</pd:description>\r\n"
				+ "    <pd:binidngs />\r\n"
				+ "    <pd:binidngs />\r\n"
				+ "    <pd:startName>\r\n"
				+ "        <pd:startName>\r\n"
				+ "            <pd:startName>Force DR</pd:startName>\r\n"
				+ "        </pd:startName>\r\n"
				+ "    </pd:startName>\r\n"
				+ "    <!-- comment\r\n"
				+ "     -->\r\n"
				+ "    <pd:xxx />\r\n"
				+ "</pd:label>";
		NodeBuilder node = xmlBuilderSAXFactory.parseDocument(sourceXml);
		assertEquals(sourceXml, node.toString());
	}
	@Test
	public void testXmlns() {
		XmlBuilderSAXFactory xmlBuilderSAXFactory = new XmlBuilderSAXFactory();
		NodeBuilder node = xmlBuilderSAXFactory.parseDocument("<a:label xmlns:a=\"somenamespace\" />");
		assertEquals(node.getNamespace(), "somenamespace");
		node = xmlBuilderSAXFactory.parseDocument("<label xmlns=\"somenamespace\" />");
		assertEquals(node.getNamespace(), "somenamespace");
		node = xmlBuilderSAXFactory.parseDocument("<a:label xmlns=\"somenamespace\" xmlns:a=\"somenamespace\" />");
		assertEquals(node.getNamespace(), "somenamespace");
		node = xmlBuilderSAXFactory.parseDocument("<aa:label xmlns=\"somenamespaceXXXX\" xmlns:aa=\"somenamespace\" />");
		assertEquals(node.getNamespace(), "somenamespace");
	}
	
	@Test
	public void testDifferenteFormatting() {
		XmlBuilderSAXFactory xmlBuilderSAXFactory = new XmlBuilderSAXFactory();
		String sourceXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ "<pd:ProcessDefinition xmlns:ipd=\"http://www.vodafone.cz/iProcessDecoupler/xml\"\r\n"
				+ "                      xmlns:pd=\"http://xmlns.tibco.com/bw/process/2003\"\r\n"
				+ "                      xmlns:BW=\"java://com.tibco.pe.core.JavaCustomXPATHFunctions\"\r\n"
				+ "                      xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\r\n"
				+ "                      xmlns:ns=\"http://www.tibco.com/pe/EngineTypes\"\r\n"
				+ "                      xmlns:ns12=\"http://www.vodafone.cz/Common/xml/Common\"\r\n"
				+ "                      xmlns:ns5=\"http://www.vodafone.cz/ServiceLayer/Charging/xml/DataTypes/v1_0\"\r\n"
				+ "                      xmlns:ns6=\"http://www.vodafone.cz/ServiceLayer/ResourceManagement/xml/DataTypes/v1_0\"\r\n"
				+ "                      xmlns:ns7=\"http://www.vodafone.cz/ServiceLayer/GSMNetwork/xml/DataTypes/v1_0\"\r\n"
				+ "                      xmlns:pfx=\"http://www.vodafone.cz/ServiceLayer/xml/InterfaceIPE/v1_0\"\r\n"
				+ "                      xmlns:ns8=\"http://www.vodafone.cz/Orchestration/xml/DataTypes/v1_0\"\r\n"
				+ "                      xmlns:ns2=\"http://www.tibco.com/namespaces/tnt/plugins/jms\"\r\n"
				+ "                      xmlns:ns1=\"http://www.tibco.com/pe/DeployedVarsType\"\r\n"
				+ "                      xmlns:ns4=\"http://www.vodafone.cz/ServiceLayer/xml/InterfaceIPEBase/v1_0\"\r\n"
				+ "                      xmlns:ns3=\"http://www.vodafone.cz/ServiceLayer/Complex/xml/DataTypes/v1_0\"\r\n"
				+ "                      xmlns:ns9=\"http://www.vodafone.cz/ServiceLayer/Network/xml/DataTypes/v1_0\"\r\n"
				+ "                      xmlns:ns10=\"http://www.vodafone.cz/ServiceLayer/InternalSupportingService/xml/DataTypes/v1_0\"\r\n"
				+ "                      xmlns:ns11=\"http://www.vodafone.cz/ServiceLayer/Crm/xml/DataTypes/v1_0\"\r\n"
				+ "                      xmlns:ns14=\"http://www.vodafone.cz/ServiceLayer/ThirdPartyService/xml/DataTypes/v1_0\"\r\n"
				+ "                      xmlns:ns15=\"http://www.vodafone.cz/ServiceLayer/Billing/xml/DataTypes/v1_0\"\r\n"
				+ "                      xmlns:pfx2=\"http://www.vodafone.cz/Common/xml/Error\"\r\n"
				+ "                      xmlns:ns16=\"http://www.vodafone.cz/Integration/Schema/iProcessAdapter\">\r\n"
				+ "   <xs:import xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\r\n"
				+ "              namespace=\"http://www.vodafone.cz/ServiceLayer/xml/InterfaceIPE/v1_0\"\r\n"
				+ "              schemaLocation=\"/Resources/Contracts/Schemas/ServiceLayer/InterfaceIPE_1_0.xsd\"/>\r\n"
				+ "   <xs:import xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\r\n"
				+ "              namespace=\"http://www.vodafone.cz/Common/xml/Error&#34;\"\r\n"
				+ "              schemaLocation=\"/Resources/Contracts/Schemas/Common/XmlErrorSchema.xsd\"/>\r\n"
				+ "   <pd:name>Processes/Dispatchers/Billing/BarringForceDelayedRelease.process</pd:name>\r\n"
				+ "   <pd:startName>\r\n"
				+ "      <pd:startName>\r\n"
				+ "         <pd:startName>Force DR</pd:startName>\r\n"
				+ "      </pd:startName>\r\n"
				+ "   </pd:startName>\r\n"
				+ "</pd:ProcessDefinition>\r\n"
				+ "";
		NodeBuilder node = xmlBuilderSAXFactory.parseDocument(sourceXml);
		assertEquals(sourceXml, node.toString());
	}
	
	@Test
	public void testSAX() {
//		XmlBuilderSAXFactory fac = new XmlBuilderSAXFactory();
//		new FileSearcher().iterateFiles(Paths.get("e:/git/integrationsourcecodes/"),"glob:**/*.process", true).parallel().map(p -> {
//			try {
//				String readAllBytes = Files.readString(p);
//				readAllBytes = normalizeEnding(readAllBytes);
//				NodeBuilder loadFromFile = fac.loadFromFile(p);
//				String string = loadFromFile.toString();
//				string = normalizeEnding(string);
//				if (!readAllBytes.equals(string)) {
//					System.out.println(p + " diff");
//					Files.write(p, string.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
//				} else {
//					System.out.println(p);
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return p;
//		}).forEach(p -> System.out.println(p));
	}
	private String normalizeEnding(String readAllBytes) {
		if (readAllBytes.charAt(readAllBytes.length() - 1) == '\n') readAllBytes = readAllBytes.substring(0, readAllBytes.length() - 1);
		if (readAllBytes.charAt(readAllBytes.length() - 1) == '\r') readAllBytes = readAllBytes.substring(0, readAllBytes.length() - 1);
		return readAllBytes;
	}
}
