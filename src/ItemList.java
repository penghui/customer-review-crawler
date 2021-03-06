import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

import org.apache.commons.io.FileUtils;

/**
 * Handles Item ID lists (merge, add, write to file etc.) Contains a Set (no
 * duplicate) of item IDs
 * 
 * @author Feng Mai
 * 
 */
public class ItemList {
	public ItemList() {
		itemIDs = new LinkedHashSet<String>();
	}

	/**
	 * @param file
	 *            A file that contains ItemList, one line each item id
	 * @throws FileNotFoundException
	 */
	public ItemList(String file) throws FileNotFoundException {
		itemIDs = new LinkedHashSet<String>();
		Scanner s = new Scanner(new File(file));
		while (s.hasNext()) {
			itemIDs.add(s.next());
		}
		s.close();
	}

	public String toString() {
		return itemIDs.toString();
	}

	public boolean addItem(String newItem) {
		return itemIDs.add(newItem);
	}

	/**
	 * For the current Item ID List, read all item reviews
	 * 
	 * @param database
	 *            The review database to be written in; if database file does not exist create a new database
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws ClassNotFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws ParseException
	 * @throws SQLException
	 */
	public void writeReviewsToDatabase(String database, boolean API) throws IOException,
			InvalidKeyException, ClassNotFoundException,
			NoSuchAlgorithmException, ParseException, SQLException {
		for (String id : itemIDs) {
			Item item = new Item(id);
			item.fetchReview();
			item.writeReviewsToDatabase(database, API);
		}
	}

	/**
	 * Merge 2 lists and remove duplicates
	 * 
	 * @param anotherList
	 */
	public void mergeList(ItemList anotherList) {
		itemIDs.addAll(anotherList.returnIDsAsSet());
	}

	/**
	 * Divide a ItemList into n smaller lists (for possible threading)
	 * @param npartition Number of separated list to be divided into
	 * @return
	 */
	public ArrayList<ItemList> divide(int npartition){
		return null;
	}
	
	
	/**
	 * Write an item list into a text file
	 * 
	 * @param filePath
	 *            plain text file to write into, one id per line
	 * @return true if successful
	 * @throws IOException
	 */
	public boolean writeToCSV(String filePath) throws IOException {
		FileUtils.writeLines(new File(filePath), itemIDs);
		return true;
	}

	public LinkedHashSet<String> returnIDsAsSet() {
		return itemIDs;
	}

	private LinkedHashSet<String> itemIDs;
}
