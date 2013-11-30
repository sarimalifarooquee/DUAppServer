package com.du.appserver;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

public class Scrapper {
	

	private static final int ID_DIRECTORY = 5;
	private static final int ID_ADMISSION = 38;
	private static final int ID_COURSES = 39;
	private static final int ID_COLLEGES = 40;
	private static final int ID_EXAMINATION = 41;
	private static final int ID_CONFERENCES = 43;
	private static final int ID_LATEST = 634;
	private static final int ID_FOREIGN = 146;

	private static final String DIRECTORY = "directory";
	private static final String ADMISSION = "admission";
	private static final String COURSES = "courses";
	private static final String COLLEGES = "colleges";
	private static final String EXAMINATION = "examination";
	private static final String CONFERENCES = "conferences";
	private static final String LATEST = "latest";
	private static final String FOREIGN = "foreign";
	private static final String REPOPULATE = "repopulate";

	private static final String REQUEST = "request";
	private static final String DU_URL_BASE = "http://du.ac.in/index.php?id=";
	
    static Cache cache;
    static Map<String, Integer> props = new HashMap<String, Integer>();	
    
    public static void InitCache(){
        props.put(GCacheFactory.EXPIRATION_DELTA, 3600);
        try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(props);
        } catch (CacheException e) {
        }

	}
    
	public static String getAndScrapCollegeInit(String sec, String url, String webUrl) {
		Document doc;
		try {
			doc = Jsoup.connect(webUrl).timeout(60000).get();
			doc.head()
			.html("<link type='text/css' rel='stylesheet' href='file:///android_asset/style.css' media='all' />"
					+ " <link type='text/css' rel='stylesheet' href='file:///android_asset/stylesheet_50a965182a.css' />"
					+ " <script type='text/javascript' src='file:///android_asset/jquery-1.2.6.min.js'></script>"
					+ " <script type='text/javascript' src='file:///android_asset/accordian.js'></script>"
					+ " <script type='text/javascript' src='file:///android_asset/jquery.js'></script>"
					+ " <script type='text/javascript' src='file:///android_asset/menu.js'></script>"
					+ " <script type='text/javascript' src='file:///android_asset/javascript_93077bb238.js'></script>");
			doc.body().html(doc.getElementsByClass("csc-header-n1").get(0).html()
							+ doc.getElementById("contentA3").html());
			doc.getElementById("PageLists").attr("id", "");
			doc.select("[src]").remove();
			doc.head().append(" <script type='text/javascript' src='file:///android_asset/jquery-1.2.6.min.js'></script>"
					+ " <script type='text/javascript' src='file:///android_asset/accordian.js'></script>"
					+ " <script type='text/javascript' src='file:///android_asset/jquery.js'></script>"
					+ " <script type='text/javascript' src='file:///android_asset/menu.js'></script>"
					+ " <script type='text/javascript' src='file:///android_asset/javascript_93077bb238.js'></script>");

			cache.put(sec+url, doc.html());
			return doc.html();
		} catch (IOException e) {
			 e.printStackTrace();
			 return e.getMessage();
		}

	}

	public static String getAndScrapGeneral(String sec, String url, String webUrl) {
		Document doc;
		try {

			doc = Jsoup.connect(webUrl).timeout(60000).get();
			doc.head()
			.html("<link type='text/css' rel='stylesheet' href='file:///android_asset/style.css' media='all' />"
					+ " <link type='text/css' rel='stylesheet' href='file:///android_asset/stylesheet_50a965182a.css' />");

			Elements midDivs = doc.getElementsByClass("mid_addresses_txt");
			Elements colDivs = doc.getElementsByClass("college-box");

			if (midDivs.size() > 0) {
				doc.body().html(midDivs.get(0).html());
			} else if (colDivs.size() > 0) {
				doc.body().html(colDivs.get(0).html());
			} else {
				
			}

			doc.select("[src]").remove();
			if(sec.equals(ADMISSION)){
				doc.getElementsByTag("tbody").get(0).html("<tr><td><a href='index.php?id=44' class='internal-link'> Announcements </a></td></tr> <tr><td><a href='index.php?id=660' class='internal-link'>Admission Test Results </a></td></tr> <tr><td><a href='index.php?id=146' class='internal-link'>Foreign Students </a></td></tr> <tr><td><a href='index.php?id=668' class='internal-link'>Query related to Admissions </a></td></tr>");
			}
			
			doc.head().append(" <script type='text/javascript' src='file:///android_asset/jquery-1.2.6.min.js'></script>"
					+ " <script type='text/javascript' src='file:///android_asset/accordian.js'></script>"
					+ " <script type='text/javascript' src='file:///android_asset/jquery.js'></script>"
					+ " <script type='text/javascript' src='file:///android_asset/menu.js'></script>"
					+ " <script type='text/javascript' src='file:///android_asset/javascript_93077bb238.js'></script>");
			cache.put(sec+url, doc.html());
			return doc.html();
		} catch (IOException e) {
			 e.printStackTrace();
			 return e.getMessage();
		}

	}

	public static String getAndScrapGenralInit(String sec, String url) {
		int id = -1;

		if (sec.equals(COLLEGES)) {
			id = ID_COLLEGES;
			return getAndScrapCollegeInit(sec, url, DU_URL_BASE + id);
		}
		if (sec.equals(DIRECTORY))
			id = ID_DIRECTORY;
		else if (sec.equals(ADMISSION))
			id = ID_ADMISSION;
		else if (sec.equals(COURSES))
			id = ID_COURSES;
		else if (sec.equals(EXAMINATION))
			id = ID_EXAMINATION;
		else if (sec.equals(CONFERENCES))
			id = ID_CONFERENCES;
		else if (sec.equals(LATEST))
			id = ID_LATEST;
		else if (sec.equals(FOREIGN))
			id = ID_FOREIGN;
		else
			return "Invalid Request!";
		return getAndScrapGeneral(sec, url, DU_URL_BASE + id);
	}

	public static void repopulateCache(){
		scrap(DIRECTORY, "");
		scrap(ADMISSION, "");
		scrap(COURSES, "");
		scrap(EXAMINATION, "");
		scrap(CONFERENCES, "");
		scrap(LATEST, "");
		scrap(FOREIGN, "");		
		scrap(COLLEGES, "");
		scrap(REQUEST, "http://www.du.ac.in/index.php?id=44");

	}

	public static String scrap(String sec, String url) {
		if(sec.equals(REPOPULATE)){
			repopulateCache();
		}
		System.out.println(sec+url);
		if(cache.containsKey(sec+url)){
			return (String)cache.get(sec+url);
		}
		
		if (sec.equals(REQUEST)) {
			return getAndScrapGeneral(sec, url, url);
		} else {
			return getAndScrapGenralInit(sec, url);
		}

	}
}
