
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.joda.time.DateTime;

/**
 * @author Feng Mai
 * Get all items' ASIN(Product IDs) in a node(product category) from amazon.com
 */
@SuppressWarnings("unused")
public class GetASINbyNode implements GetID{
	/**
	 * Example (Tablet PCs):
	 * 	getIDsByNode getid = new getIDsByNode("541966%2C1232597011",1,300);
	 * @param thenode node (category) from Amazon
	 * @param fromPage the first page
	 * @param toPage the last page, each page has 5 products, so toPage = 300 will parse at most 1500 product IDs from the category
	 */
	public GetASINbyNode(String thenode, int fromPage, int toPage){
		this.nodeid = thenode;
		this.from = fromPage;
		this.to = toPage;
		this.items = new ItemList();
	}

	public void updateCSV(String oldfile) throws MalformedURLException, IOException{
		ItemList oldlist = new ItemList(oldfile);
		oldlist.mergeList(items);
		oldlist.writeToCSV(oldfile);
	}
	
	public void getIDList() throws IOException{
		for (int i = from; i <= to; i++) {
//			String url = "http://www.amazon.com/gp/aw/s/ref=is_pg_2_1?n="+nodeid+"&p="+i+"&p_72=1248882011&s=salesrank";
			String url = "http://www.amazon.com/b/ref=dp_brw_link?ie=UTF8&node=" + nodeid + "&page=" + i;
			String thepage = readWebPage(url);
			System.out.println(url);
			System.out.println(thepage);
			DateTime dt = new DateTime();
			System.out.println(dt+ "Page "+i);
//			String patternString = "(<a href=\"/gp/aw/d/)(\\S{10})(/ref=mp_s_a)";
			String patternString = "(/dp/)([0-9A-Z]+)";
			Pattern pattern = Pattern.compile(patternString);
			Matcher matcher = pattern.matcher(thepage);
			while (matcher.find()) {
				boolean success = items.addItem(matcher.group(2));
//				System.out.println("Item " + matcher.group(2) + " added: " + (String.valueOf(success)));
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				//ignore
			}
		}
	}

	public void getIDList2() throws IOException {
		boolean end = false;
		String url = "";
		for (int i = from; i <= to; i++) {
			if(url.length() == 0) url = "http://www.amazon.com/b/ref=dp_brw_link?ie=UTF8&node=" + nodeid + "&page=" + i;
			String pageContent = null;
			pageContent = readContent(url, 3);
			addIdsWithPageContent(pageContent);
			ArrayList nextPageUrlAndPageNumber = getNextPageUrlAndPageNumber(pageContent);
			int nextPageNumber = (Integer)nextPageUrlAndPageNumber.get(1);
			System.out.println("next page: "+nextPageNumber+" next i:"+(i+1));
			if(nextPageNumber != (i+1)) break;
			url = (String)nextPageUrlAndPageNumber.get(0);
			if(!url.startsWith("http://www.amazon.com")) url = "http://www.amazon.com" + url;
		}
	}
	private void addIdsWithPageContent(String pageContent) {
		String patternString = "(/dp/)([0-9A-Z]+)";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(pageContent);
		while (matcher.find()) {
			boolean success = items.addItem(matcher.group(2));
			if(success) System.out.println("Item " + matcher.group(2) + " added: " + (String.valueOf(success)));
		}
	}
	private String readContent(String url, int retryTimes) throws IOException {
			try {
				System.out.println("Getting url:" + url);
				return readWebPage(url);
			} catch(IOException e) {
				if(retryTimes>0) {
					try {
						System.out.println("sleep 3000 milli-seconds, retry: " + retryTimes);
						Thread.sleep(3000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					return readContent(url, --retryTimes);
				}
				else throw e;
			}
		}

	private ArrayList getNextPageUrlAndPageNumber(String pageContent) {
		ArrayList result = new ArrayList();
		String patternString = "<a [^>]* id=\"pagnNextLink\"[^>]*href=\"([^\"]+page=([0-9]+)[^\"]+)\"[^>]*[>]";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(pageContent);
		if(matcher.find()) {
			result.add(matcher.group(1));
			result.add(Integer.valueOf(matcher.group(2)));
		} else {
			result.add("");
			result.add(-1);
		}
		return result;
	}
	/**
	 * Write all ASIN in the node to a csv file
	 * @param filePath The file path to write all ASIN in a node to
	 * @throws IOException 
	 */
	public void writeIDsToCSV(String filePath) throws IOException {
		items.writeToCSV(filePath);
	}
	
	/**
	 * Returns a webpage's html code
	 * @param weburl The URL to read webpage from
	 * @return return a string that contains the HTML code of the webpage
	 * @throws IOException
	 */
	public String readWebPage(String weburl) throws IOException{
		HttpClient httpclient = new DefaultHttpClient();
		//HttpProtocolParams.setUserAgent(httpclient.getParams(), "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
		HttpGet httpget = new HttpGet(weburl);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();    
		String responseBody = httpclient.execute(httpget, responseHandler);
		// responseBody now contains the contents of the page
		// System.out.println(responseBody);
		httpclient.getConnectionManager().shutdown();
		return responseBody;
	}
	private ItemList items;
	private String nodeid;
	private int from;
	private int to;
}
