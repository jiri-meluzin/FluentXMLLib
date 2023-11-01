package com.meluzin.fluentxml.xml.builder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

public class XmlBuilderSAXFactory extends XmlBuilderFactory {
	private static final XmlBuilderSAXFactory singleton = new XmlBuilderSAXFactory();
	public static XmlBuilderSAXFactory getSingleton() {
		return singleton;
	}
	@Override
	public void renderNode(NodeBuilder node, boolean prettyPrint, OutputStream output) {
		renderNode(node, settingsMap.getOrDefault(node, Settings.DEFAULTS), prettyPrint, output);
	}
	public void renderNode(NodeBuilder node, Settings settings, boolean prettyPrint, OutputStream output) {
		try {
			new SAXRenderHandler(settings).render(node, output, prettyPrint);
		} catch (XMLStreamException | FactoryConfigurationError | IOException e) {
			throw new RuntimeException("Could not render XML document", e);
		}
	}
	@Override
	public void renderNode(NodeBuilder node, OutputStream output) {
		renderNode(node, true, output);
	}

	@Override
	public String renderNode(NodeBuilder node, boolean prettyPrint) {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			renderNode(node, prettyPrint, output);
			return new String(output.toByteArray(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Could not render XML document", e);
		}
	}

	private Map<NodeBuilder, Settings> settingsMap =  Collections.synchronizedMap(new WeakHashMap<>());

	@Override
	public NodeBuilder parseDocument(InputStream input) {
		try (InputStream inputStream = input) {
			String data = IOUtils.toString(input, StandardCharsets.UTF_8);
			String recognizedCharset = (data.contains("\n")? data.substring(0, data.indexOf('\n')-1):data).trim().replaceAll(".*\\<\\?xml version\\s*=\\s*\\\"1.0\\\" encoding\\s*=\\s*\\\"([^\\\"]+)\\\".*", "$1");
			boolean isTherePlainGreaterThenInAttribute = comparePattern(data, "(?s)=\\\"[^\\\"]*>[^\\\"]*\\\"", "(?s)=\\\"[^\\\"]*&gt;[^\\\"]*\\\"") > 0;
			boolean isOldFormatting = comparePattern(data, "(?s)>(\r?)\n   <","(?s)>(\r?)\n    <") > 0;
			EndOfFileType isNewLineAtTheEnd =
					data.endsWith("\r") ? EndOfFileType.Mac :
						data.endsWith("\r\n") ? EndOfFileType.Windows:
							data.endsWith("\n") ? EndOfFileType.Linux:
								EndOfFileType.None;
			boolean isBom = data.startsWith(""+(char)Settings.BOM_CHAR);
			Settings settings = Settings.builder().
					spaceInEmptyTag(data.contains(" />")).
					charset(recognizedCharset).
					BOM(isBom).
					escapeGreaterThanChar(!isTherePlainGreaterThenInAttribute).
					oldFormatting(isOldFormatting).
					newLineAtTheEnd(isNewLineAtTheEnd).
				build();
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			spf.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
			SAXParser saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			SAXParserHandler handler = new SAXParserHandler();
			xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
			ByteArrayInputStream input2 = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
			NodeBuilder node = handler.load(this, input2, saxParser);
			settingsMap.put(node, settings);
			return node;
		} catch (IOException | ParserConfigurationException | SAXException e) {
			throw new RuntimeException("Could not parse XML document from stream", e);
		}
	}

	private static int detectPattern(String data, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(data);
		int count = 0;
		while (matcher.find()) {
			count ++;
		}
		return count;
	}

	private static int comparePattern(String data, String positivePattern, String negativePattern) {
		String regex = "(?s)=\\\"[^\\\"]*>[^\\\"]*\\\"";
		return detectPattern(data, positivePattern)-detectPattern(data, negativePattern);
	}
	public static void main(String[] args) {
		System.out.println(comparePattern("a b c e a b c", "[a]","[e]"));
	}

	@Builder
	@SuppressWarnings("unused")
	@Getter
	public static class Settings {
		private static final Settings DEFAULTS = Settings.builder().build();
		public static final char BOM_CHAR = (char)65279;
		@Builder.Default
		private boolean spaceInEmptyTag = false;
		@Builder.Default
		private String charset = "UTF-8";
		@Builder.Default
		private boolean BOM = false;
		/**
		 * Whether to escape character &gt; as &amp;gt;
		 */
		@Builder.Default
		private boolean escapeGreaterThanChar = false;
		/**
		 * In case of BW <5.14 there was formatting, that each attribute was on separate line and spacing was 3space, not 4spaces
		 */
		@Builder.Default
		private boolean oldFormatting = false;

		@Builder.Default
		private String padding = "    ";

		@Builder.Default
		private EndOfFileType newLineAtTheEnd = EndOfFileType.getSystemDefault();
	}
	public enum EndOfFileType {
		Linux("\n"),
		Windows("\r\n"),
		Mac("\r"),
		None("");
		private String ending;
		private EndOfFileType(String ending) {
			this.ending = ending;
		}
		public String getEnding() {
			return ending;
		}
		public static EndOfFileType getSystemDefault() {
			return Arrays.asList(values()).stream().filter(v -> v.getEnding().equals(System.lineSeparator())).findFirst().orElse(Windows);
		}
	}
}
