package legedit2.cardtype;

import javax.swing.JTextField;

import org.w3c.dom.Node;

import legedit2.card.Card;

public class ElementProperty extends CustomElement {
	
	public CustomProperties property;
	public String defaultValue;
	
	public String value;
	
	private JTextField propertyField;

	public String getValue()
	{
		if (value != null)
		{
			return value;
		}
		return defaultValue;
	}
	
	public String generateOutputString()
	{
		return generateOutputString(false);
	}
	
	public String generateOutputString(boolean fullExport)
	{
		String str = "";
		if (value != null)
		{
			str += "CUSTOMVALUE;" + name + ";value;" + value + "\n";
		}
		
		str += "CUSTOMVALUE;" + name + ";visible;" + visible + "\n";
		
		return str;
	}

	public JTextField getPropertyField() {
		return propertyField;
	}

	public void setPropertyField(JTextField propertyField) {
		this.propertyField = propertyField;
	}
	
	@Override
	public void updateCardValues()
	{
		if (propertyField != null)
		{
			value = propertyField.getText();
		}
	}
	
	@Override
	public void loadValues(Node node, Card card)
	{
		if (!node.getNodeName().equals("property"))
		{
			return;
		}
		
		if (node.getAttributes().getNamedItem("value") != null)
		{
			value = node.getAttributes().getNamedItem("value").getNodeValue();
			value = restoreNonXMLCharacters(value);
		}
	}
	
	@Override
	public String getDifferenceXML()
	{
		String str = "";
		
		str += "<property name=\"" + replaceNonXMLCharacters(name) + "\" value=\""+replaceNonXMLCharacters(getValue())+"\" />\n";
		
		return str;
	}
}
