package com.chs.connectors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		
		 //Get the DOM Builder Factory
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	    //Get the DOM Builder
	    DocumentBuilder builder = factory.newDocumentBuilder();

	    String p = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Report>  <DataUnit type=\"daily\">  	<concept>Ebola</concept>        <gender>Male</gender>        <timestamp>2015-03-12</timestamp>        <value>3</value>   </DataUnit></Report>";

	    //Load and Parse the XML document
	    //document contains the complete XML as a Tree.
//	    Document document = builder.parse(ClassLoader.getSystemResourceAsStream("\\com\\chs\\connectors\\data.xml"));
	    Document document = builder.parse(new ByteArrayInputStream(p.getBytes(StandardCharsets.UTF_8)));
	    List<Report> empList = new ArrayList<Report>();

	    //Iterating through the nodes and extracting the data.
	    NodeList nodeList = document.getDocumentElement().getChildNodes();

	    for (int i = 0; i < nodeList.getLength(); i++) {

	      //We have encountered an <employee> tag.
	      Node node = nodeList.item(i);
	      if (node instanceof Element) {
	        Report emp = new Report();
	        emp.frequency = node.getAttributes().
	            getNamedItem("type").getNodeValue();

	        NodeList childNodes = node.getChildNodes();
	        for (int j = 0; j < childNodes.getLength(); j++) {
	          Node cNode = childNodes.item(j);

	          //Identifying the child tag of employee encountered. 
	          if (cNode instanceof Element) {
	            String content = cNode.getLastChild().getNodeValue().trim();
	            switch (cNode.getNodeName()) {
	              case "concept":
	                emp.concept = content;
	                break;
	              case "gender":
	                emp.dissagregation = content;
	                break;
	              case "timestamp":
	                emp.timestamp = content;
	                break;
	              case "value":
		            emp.dataValue = Integer.parseInt(content);
		            break;
	            }
	          }
	        }
	        empList.add(emp);
	      }

	    }

	    //Printing the Employee list populated.
	    for (Report emp : empList) {
	      System.out.println(emp);
	    }

}
	
}
