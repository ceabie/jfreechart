package org.jfree.data.io;

import org.jfree.data.category.CategoryDataset;
import org.junit.Test;

import java.io.FileReader;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2016/5/9.
 */
public class CSVTest {
    @Test
    public void testCSV() {
        CSV csv = new CSV(',', '\'');

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        try {
            FileReader fileReader = new FileReader("000001.csv");

            CategoryDataset categoryDataset = csv.readCategoryDataset(fileReader);
            int rowCount = categoryDataset.getRowCount();
//            for (int i=1; i<rowCount; i++) {
//                System.out.println(simpleDateFormat.parse(categoryDataset.get));
//            }
            System.out.println(categoryDataset.getValue(0, 0));
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
