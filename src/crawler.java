import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import java.sql.SQLException;
import java.text.ParseException;
import java.io.IOException;

public class crawler {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ParseException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, ParseException,
			ClassNotFoundException, SQLException, InvalidKeyException,
			NoSuchAlgorithmException, InterruptedException {

//		//example: write all reviews for Samsung Tab 3 to a SQLite database
//		Item samsungTab3 = new Item("B00D02AGU4");
//		samsungTab3.fetchReview();
//		samsungTab3.writeReviewsToDatabase("/Users/ph/reviewtest.db", false);




////		http://www.amazon.com/cell-phone-accessories/b/ref=dp_brw_link?ie=UTF8&node=2407755011
////		http://www.amazon.com/b/ref=dp_brw_link?ie=UTF8&node=2407755011&page=2
////		http://www.amazon.com/gp/aw/s/ref=is_pg_2_1?n=2407755011&p=1&p_72=1248882011&s=salesrank
//		GetASINbyNode getTabletid = new GetASINbyNode("2407755011", 1, 100);
//		getTabletid.getIDList2();
//		getTabletid.writeIDsToCSV("/Users/ph/phoneAccessories_idlist_100pages.txt");


		ItemList thelist = new ItemList("/Users/ph/phoneAccessories_idlist_100pages.txt");
		thelist.writeReviewsToDatabase("/Users/ph/phoneAccessories_reviews_100pages.db", false);
	}

}
