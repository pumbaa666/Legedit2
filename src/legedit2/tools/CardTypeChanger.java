package legedit2.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CardTypeChanger
{
	public static void main(String[] args)
	{
		String directoryLocation = "D:\\print'n'play\\Legendary Encounters - Harry Potter\\";
		String fileName = "cards_18.txt";
		String modifiedFileName = "cards_18b.txt";
		File file = new File(directoryLocation + fileName);
		File modifiedFile = new File(directoryLocation + modifiedFileName);
		try
		{
			if(modifiedFile.exists())
				new PrintWriter(modifiedFile).close(); // Clear the file
			else
				modifiedFile.createNewFile();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		try (BufferedReader br = new BufferedReader(new FileReader(file)); BufferedWriter bw = new BufferedWriter(new FileWriter(modifiedFile)))
		{
			String line;
			while((line = br.readLine()) != null)
			{
				line = line.replace("hero_common", "hero_rare")
						.replace("hero_uncommon", "hero_rare")
						.replace("icon name=\"Power Overlay\"", "icon name=\"Power\"");
				
				if(line.contains("textarea") && line.contains("textsize"))
					line = line.replaceAll("textsize=\"[0-9]+\"", "textsize=\"26\"");
				
				bw.write(line + "\n");
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
