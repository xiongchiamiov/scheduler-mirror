package scheduler.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import scheduler.model.db.DatabaseException;

public class CSVGenerateTestData {
	private static int numOfItems = 30;
	final static String exportFilePath = "test/scheduler/model/CSVExportOracle/";
	final static String exportFileName = "exportCase";

	public static void main(String[] args) {

		CSVTest test = new CSVTest();
		for (int items = 0; items < 100; items++)
			test.generateTestData(items, exportFilePath, exportFileName);
		test.generateTestData(1000, exportFilePath, exportFileName);

	}

}
