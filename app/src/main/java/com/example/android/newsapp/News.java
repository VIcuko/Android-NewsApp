package com.example.android.newsapp;

import java.util.ArrayList;

/**
 * Created by Vicuko on 10/8/17.
 */

public class News {
    private String mWebTitle;
    private ArrayList<String> mAuthors;
    private String mSectionName;
    private String mwebPublicationDate;

    public News(String title, ArrayList<String> authors, String sectionName,
                String publishedDate){

        mWebTitle = title;
        mAuthors = authors;
        mSectionName = sectionName;
        mwebPublicationDate = publishedDate;
    }

    public String getTitle(){
        return mWebTitle;
    }

    public ArrayList<String> getAuthors(){
        return mAuthors;
    }

    public String getSectionName(){
        return mSectionName;
    }

    public String getPublishedDate(){
        return mwebPublicationDate;
    }
}
