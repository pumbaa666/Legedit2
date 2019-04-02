package legedit2.gui.project;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

class PopupListener extends MouseAdapter
{

	private JPopupMenu popup;

	public PopupListener(JPopupMenu popup)
	{
		this.popup = popup;
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		maybeShowPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e)
	{
		if(e.isPopupTrigger())
			popup.show(e.getComponent(), e.getX(), e.getY());
	}
}
