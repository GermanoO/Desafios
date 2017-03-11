package backup;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import javax.swing.filechooser.FileNameExtensionFilter;

// Atenção este FileUtils foi ajustado para só trabalhar com .txt e .xml
public class FileUtils {

	
	public static File[] dirListByAscendingDate(File folder) {
		if (!folder.isDirectory()) {
			return null;
		}

		FilenameFilter filenameFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				int index = -1;
				if (name.endsWith(".txt")) {
					index = name.toLowerCase().indexOf(".txt");
				}
				if (index > -1) {
					return true;
				} else {
					return false;
				}
			}
		};

		File files[] = folder.listFiles(filenameFilter);
		return files;
	}
	
}