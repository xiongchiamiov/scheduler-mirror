package scheduler.model;

import scheduler.model.simpledb.CSVTestSimpleDB;

public class CSVGenerateTestData {
	private static int numOfItems = 30;
	final static String exportFilePath = "test/scheduler/model/CSVExportOracle/";
	final static String exportFileName = "exportCase";

	public static void main(String[] args) {

		CSVTest test = new CSVTestSimpleDB();
		for (int items = 0; items < 100; items++)
			test.generateTestData(items, exportFilePath, exportFileName);
		test.generateTestData(1000, exportFilePath, exportFileName);

	}

}
