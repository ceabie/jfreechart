package com.ceabie.util;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.io.CSV;
import org.jfree.ui.ApplicationFrame;

import java.io.File;
import java.io.FileReader;

/**
 * Created by Administrator on 2016/5/15.
 */
public class FileUtil extends ApplicationFrame {
    public FileUtil(String title) {
        super(title);
    }

    protected static CategoryDataset getCategoryDataset(String dataFile) {
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
