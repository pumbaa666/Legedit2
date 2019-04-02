package legedit2.imaging;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.BufferedWriter;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import legedit2.card.Card;
import legedit2.cardtype.CardType;
import legedit2.cardtype.CustomElement;
import legedit2.definitions.Icon;

public class CustomCardMaker
{

	public String exportFolder = "cardCreator";
	public static String templateFolder = "legedit" + File.separator + "cardtypes";

	private boolean debug = false;

	// 2.5 by 3.5 inches - Poker Size
	public static int cardWidth = 750;
	public static int cardHeight = 1050;
	public static int dpi = 300;

	boolean exportImage = false;
	public boolean exportToPNG = true;

	public Card card;

	public BufferedWriter bwErr = null;

	public CardType template = null;

	private double scale = 1.0d;

	public CustomCardMaker()
	{

	}

	public void setCard(Card c)
	{
		card = c;
		template = c.getTemplate();
	}

	public void populateCard()
	{
		card = new Card();
	}

	public void populateBlankCard()
	{
		card = new Card();
	}

	public static Card getBlankCard()
	{
		Card card = new Card();
		return card;
	}

	public BufferedImage generateCard()
	{
		int type = BufferedImage.TYPE_INT_ARGB;
		if(exportToPNG)
			type = BufferedImage.TYPE_INT_ARGB;
		
		BufferedImage image = new BufferedImage(getPercentage(cardWidth, getScale()), getPercentage(cardHeight, getScale()), type);
		Graphics2D g = (Graphics2D)image.getGraphics();

		if(debug)
		{
			g.setColor(Color.black);
			g.drawRect(0, 0, image.getWidth() - 1, image.getHeight() - 1);
		}

		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g = setGraphicsHints(g);

		if(template != null)
		{
			for(CustomElement element:template.elements)
			{
				element.setScale(scale);
				element.drawElement(g);
			}

			if(template.getStyle() != null)
			{
				for(CustomElement element:template.getStyle().getElements())
				{
					element.setScale(scale);
					element.drawElement(g);
				}
			}
		}

		if(exportImage)
			exportImage(image);

		g.dispose();

		return image;
	}

	public void exportImage(BufferedImage image)
	{
		try
		{
			if(exportToPNG)
			{
				File newFile = new File(exportFolder + File.separator + card.getCardName(exportFolder) + ".png");
				exportToPNG(image, newFile);
			}
			else
			{
				File newFile = new File(exportFolder + File.separator + card.getCardName(exportFolder) + ".jpg");
				exportToJPEG(image, newFile);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void exportToJPEG(BufferedImage image, File newFile) throws Exception
	{
		BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.getGraphics();
		g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		g.dispose();

		File dir = new File(exportFolder);
		dir.mkdirs();

		ImageIO.write(bi, "jpeg", newFile);
	}

	public void exportToPNG(BufferedImage image, File newFile) throws Exception
	{
		ImageIO.write(image, "png", newFile);
	}

	public BufferedImage getIcon(Icon icon, int maxWidth, int maxHeight)
	{
		ImageIcon ii = new ImageIcon(icon.getImagePath());
		double r = 1d;
		double rX = maxWidth / (double)ii.getIconWidth();
		double rY = maxHeight / (double)ii.getIconHeight();
		if(rY < rX)
		{
			r = rY;
		}
		else
		{
			r = rX;
		}

		return resizeImage(ii, r);
	}

	public BufferedImage getIconMaxHeight(Icon icon, int maxHeight)
	{
		ImageIcon ii = new ImageIcon(icon.getImagePath());
		double r = maxHeight / (double)ii.getIconHeight();

		return resizeImage(ii, r);
	}

	public int getPercentageValue(int value, int max)
	{
		return (int)Math.round((value / (double)max) * 100d);
	}

	public int getPercentage(int size, double scale)
	{
		return (int)((size * scale));
	}

	public BufferedImage resizeImage(ImageIcon imageIcon, double scale)
	{
		int w = (int)(imageIcon.getIconWidth() * scale);
		int h = (int)(imageIcon.getIconHeight() * scale);
		int type = BufferedImage.TYPE_INT_ARGB;

		if(w <= 0 || h <= 0)
			return null;

		BufferedImage image = new BufferedImage(w, h, type);
		Graphics g = image.getGraphics();
		g.drawImage(imageIcon.getImage(), 0, 0, w, h, 0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight(), null);
		g.dispose();

		return image;
	}

	public BufferedImage resizeImage(ImageIcon imageIcon, int width, int height)
	{
		int type = BufferedImage.TYPE_INT_ARGB;

		BufferedImage image = new BufferedImage(width, height, type);
		Graphics g = image.getGraphics();

		g.drawImage(imageIcon.getImage(), 0, 0, width, height, 0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight(), null);
		g.dispose();

		return image;
	}

	public static ConvolveOp getGaussianBlurFilter(int radius, boolean horizontal)
	{
		if(radius < 1)
			throw new IllegalArgumentException("Radius must be >= 1");

		int size = radius * 2 + 1;
		float[] data = new float[size];

		float sigma = radius / 3.0f;
		float twoSigmaSquare = 2.0f * sigma * sigma;
		float sigmaRoot = (float)Math.sqrt(twoSigmaSquare * Math.PI);
		float total = 0.0f;

		for(int i = -radius; i <= radius; i++)
		{
			float distance = i * i;
			int index = i + radius;
			data[index] = (float)Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
			total += data[index];
		}

		for(int i = 0; i < data.length; i++)
			data[i] /= total;

		Kernel kernel = null;
		if(horizontal)
			kernel = new Kernel(size, 1, data);
		else
			kernel = new Kernel(1, size, data);
		return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
	}

	public BufferedImage resizeImagePS(BufferedImage bi)
	{
		//Resize for printer studio
		double scale = 2.0;
		double xPadding = 0.043;
		double yPadding = 0.08;
		String exportType = "jpg"; //png or jpg

		ImageIcon imageIcon = new ImageIcon(bi);

		int w = (int)(imageIcon.getIconWidth() * scale);
		int xPad = (int)((imageIcon.getIconWidth() * scale) * xPadding);
		int fullW = w + xPad + xPad;
		int h = (int)(imageIcon.getIconHeight() * scale);
		int yPad = (int)((imageIcon.getIconHeight() * scale) * yPadding);
		int fullH = h + yPad + yPad;
		int type = BufferedImage.TYPE_INT_ARGB;
		if(exportType.equals("jpg"))
		{
			type = BufferedImage.TYPE_INT_RGB;
		}
		BufferedImage image = new BufferedImage(fullW, fullH, type);
		Graphics g = image.getGraphics();

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, fullW, fullH);

		g.drawImage(imageIcon.getImage(), xPad, yPad, w + xPad, h + yPad, 0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight(), null);

		g.dispose();

		return image;
	}

	private Graphics2D setGraphicsHints(Graphics2D g2)
	{
		g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		return g2;
	}

	public double getScale()
	{
		return scale;
	}

	public void setScale(double scale)
	{
		this.scale = scale;
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}
}