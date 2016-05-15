package com.ceabie.util;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.io.CSV;

import java.io.File;
import java.io.FileReader;

/**
 * The type File util.
 *
 * @author ceabie
 */
public class FileUtil {

    public static CategoryDataset getCategoryDataset(String dataFile) {
        CategoryDataset categoryDataset = null;

        File serFile = new File(dataFile + ".ser");
        if (serFile.exists()) {
            categoryDataset = IOUtil.importObjectFromFile(serFile, CategoryDataset.class);
        }

        if (categoryDataset == null) {
            FileReader fileReader = null;
            try {
                CSV csv = new CSV();
                fileReader = new FileReader(dataFile);
                categoryDataset = csv.readCategoryDataset(fileReader);

                IOUtil.saveObjectToFile(serFile, categoryDataset);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtil.safeClose(fileReader);
            }
        }

        return categoryDataset;
    }
}
