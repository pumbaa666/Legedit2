package legedit2.exporters;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import legedit2.card.Card;
import legedit2.deck.Deck;
import legedit2.definitions.LegeditItem;
import legedit2.helpers.LegeditHelper;
import legedit2.helpers.ProjectHelper;
import legedit2.imaging.CustomCardMaker;

public class LegeditExporterSingleImages extends LegeditExporter {
	
	private File exportDirectory;
	
	@Override public int getExportCount()
	{
		return ProjectHelper.getDistinctCardCount();
	}
	
	@Override public void export(File exportDirectory)
	{
		this.exportDirectory = exportDirectory;
		
		setMaxValue(getExportCount());
		setCurrentValue(0);
		
		Task task = new Task();
		
		task.execute();
		
		return;
	}
	
	class Task extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			
			try
			{
				getDialog().getProgressBar().setValue(0);
				getDialog().getProgressBar().setMaximum(getMaxValue());
				
				List<LegeditItem> items = ProjectHelper.getLegeditItems();
				
				CustomCardMaker maker = new CustomCardMaker();
				maker.setScale(1.0d);
				
				int value = 0;
				for (LegeditItem i : items)
				{
					if (i instanceof Deck)
					{
						for (Card c : ((Deck)i).getCards())
						{
							maker.setCard(c);
							BufferedImage bi = maker.generateCard();
							exportImage(bi, c, exportDirectory);
							
							value++;
							getDialog().getProgressBar().setValue(value);
						}
					}
					
					if (i instanceof Card)
					{
						maker.setCard((Card)i);
						BufferedImage bi = maker.generateCard();
						exportImage(bi, (Card)i, exportDirectory);
						
						value++;
						getDialog().getProgressBar().setValue(value);
					}
				}
				
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(getDialog(), ex.getMessage() != null ? ex.getMessage() : LegeditHelper.getErrorMessage(), LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
				
			}
			
			getDialog().setVisible(false);
			
			return null;
		}
	}
}
