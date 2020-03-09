package legedit2.cardtype;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import org.w3c.dom.Node;
import legedit2.card.Card;
import legedit2.definitions.Icon;
import legedit2.definitions.Icon.ICON_TYPE;
import legedit2.imaging.CustomCardMaker;

public class ElementIconImage extends CustomElement {
	
	public Icon defaultValue = Icon.valueOf("NONE");
	public boolean allowChange = false;
	public boolean optional = false;
	public int x;
	public int y;
	public int maxWidth = Integer.MAX_VALUE;
	public int maxHeight = Integer.MAX_VALUE;
	public boolean drawUnderlay;
	public int blurRadius;
	public boolean blurDouble;
	public int blurExpand;
	public Color blurColour;
	public ICON_TYPE iconType;
	
	public int imageX;
	public int imageY;
	public int imageMaxWidth;
	public int imageMaxHeight;
	public String imagePrefix;
	public String imageExtension;
	public String imageFilter;
	
	//User values
	public Icon value;
	
	private JComboBox<Icon> iconCombobox;
	
    @Override
	public void drawElement(Graphics2D g)
	{
		// Draw BG
		String file = CustomCardMaker.templateFolder + File.separator 
				+ template.getTemplateName()
				+ File.separator + (imagePrefix != null ? imagePrefix : "") 
				+ getIconValue().getEnumName() 
				+ (imageExtension != null ? imageExtension : "");
		
		
		if (file != null && new File(file).exists())
		{	
			BufferedImage bi = null;
			if (imageFilter == null)
			{
				try
				{
					bi = resizeImage(ImageIO.read(new File(file)), getPercentage(CustomCardMaker.cardWidth,getScale()), getPercentage(CustomCardMaker.cardHeight,getScale()));				
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}

			}
			if (imageFilter != null && imageFilter.equalsIgnoreCase("dualclass") && !getIconValue().getEnumName().equals("NONE"))
			{
				try
				{
					bi = resizeImage(ImageIO.read(new File(file)), getPercentage(CustomCardMaker.cardWidth,getScale()), getPercentage(CustomCardMaker.cardHeight,getScale()));				
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}

			}
			
			if (rotate != 0)
			{
				double rotationRequired = Math.toRadians (rotate);
				double locationX = bi.getWidth() / 2;
				double locationY = bi.getHeight() / 2;
				AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
				bi = op.filter(bi, null);
			}
			
			g.drawImage(bi, getPercentage(imageX,getScale()), getPercentage(imageY,getScale()), null);
		}
		
		// Draw Icon
		if (visible && getIconValue().getImagePath() != null)
		{
			BufferedImage bi = getIcon(getIconValue(), getPercentage(maxWidth,getScale()), getPercentage(maxHeight,getScale()));
			int xStart = getPercentage(x,getScale()) - (bi.getWidth() / 2);
	    	int yStart = getPercentage(y,getScale()) - (bi.getHeight() / 2);
	    	
	    	if (drawUnderlay)
	    		drawUnderlay(bi, g, BufferedImage.TYPE_INT_ARGB, xStart, yStart, getPercentage(blurRadius,getScale()), blurDouble, getPercentage(blurExpand,getScale()), blurColour);
	    	
	    	if (rotate != 0)
			{
				double rotationRequired = Math.toRadians (rotate);
				double locationX = bi.getWidth() / 2;
				double locationY = bi.getHeight() / 2;
				AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
				bi = op.filter(bi, null);
			}
	    	
	    	g.drawImage(bi, xStart, yStart, null);
		}
	}
	
	public Icon getIconValue()
	{
		if (value != null)
			return value;

		if (defaultValue != null)
			return defaultValue;

		return Icon.valueOf("NONE");
	}
	
    @Override
	public String generateOutputString()
	{
		return generateOutputString(false);
	}
	
    @Override
	public String generateOutputString(boolean fullExport)
	{
		String str = "";
		if (value != null)
			str += "CUSTOMVALUE;" + name + ";value;" + value + "\n";
		
		str += "CUSTOMVALUE;" + name + ";visible;" + visible + "\n";
		
		return str;
	}

	public JComboBox<Icon> getIconCombobox() {
		return iconCombobox;
	}

	public void setIconCombobox(JComboBox<Icon> iconCombobox) {
		this.iconCombobox = iconCombobox;
	}
	
	@Override
	public void updateCardValues()
	{
		if (iconCombobox != null)
			value = (Icon)iconCombobox.getSelectedItem();
	}
	
    @Override
	public void loadValues(Node node, Card card)
	{
		if (!node.getNodeName().equals("iconbg"))
			return;
		
		if (node.getAttributes().getNamedItem("value") != null)
			value = Icon.valueOf(node.getAttributes().getNamedItem("value").getNodeValue());
	}
	
    @Override
	public String getDifferenceXML()
	{
		return "<iconbg name=\"" + replaceNonXMLCharacters(name) + "\" value=\""+getIconValue().getEnumName()+"\" />\n";
	}
}
