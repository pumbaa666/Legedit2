package legedit2.gui.templatemanager;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import legedit2.cardtype.CardType;
import legedit2.definitions.Icon;

public class TemplateCellRenderer extends DefaultListCellRenderer
{

	private static final long serialVersionUID = 8210293236502786270L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		CardType villain = (CardType)value;
		JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		String s = villain.getTemplateDisplayName();
		label.setText(s);

		return label;
	}

	public BufferedImage getCardIcon(Icon icon, int maxWidth, int maxHeight)
	{
		if(icon == null || icon.getImagePath() == null)
		{
			int type = BufferedImage.TYPE_INT_ARGB;
			BufferedImage image = new BufferedImage(maxWidth, maxHeight, type);
			return image;
		}

		ImageIcon ii = new ImageIcon(icon.getImagePath());
		double r = 1d;
		double rX = maxWidth / (double)ii.getIconWidth();
		double rY = maxHeight / (double)ii.getIconHeight();

		if(rY < rX)
			r = rY;
		else
			r = rX;

		return resizeImage(ii, r);
	}

	public BufferedImage resizeImage(ImageIcon imageIcon, double scale)
	{
		int w = (int)(imageIcon.getIconWidth() * scale);
		int h = (int)(imageIcon.getIconHeight() * scale);
		int type = BufferedImage.TYPE_INT_ARGB;

		BufferedImage image = new BufferedImage(w, h, type);
		Graphics g = image.getGraphics();

		g.drawImage(imageIcon.getImage(), 0, 0, w, h, 0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight(), null);

		g.dispose();

		return image;
	}
}
