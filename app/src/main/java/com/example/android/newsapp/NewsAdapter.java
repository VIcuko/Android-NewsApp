package com.example.android.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Vicuko on 10/8/17.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, ArrayList<News> newsArray) {
        super(context, 0, newsArray);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;
        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        final News oneNews = getItem(position);

        TextView title = (TextView) listView.findViewById(R.id.title);
        title.setText(oneNews.getTitle());

        TextView author = (TextView) listView.findViewById(R.id.authors);
        author.setText(parseAuthors(oneNews.getAuthors()));

        TextView year = (TextView) listView.findViewById(R.id.published_date);
        year.setText(oneNews.getPublishedDate());

        TextView section = (TextView) listView.findViewById(R.id.section);
        section.setText(oneNews.getSectionName());

        RelativeLayout itemLayout = (RelativeLayout) listView.findViewById(R.id.item_layout);
        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                openSite(oneNews.getUrl());
            }
        };
        itemLayout.setOnClickListener(onClickListener);

        return listView;
    }

    private String parseAuthors(ArrayList<String> authors) {
        String authorsTogether = "";
        if (authors != null) {
            for (int i = 0; i < authors.size(); i++) {
                authorsTogether = authorsTogether.concat(authors.get(i).toString());
                String separator;
                if (i == authors.size() - 2) {
                    separator = " and ";
                } else {
                    separator = (i == (authors.size() - 1)) ? ("") : (", ");
                }
                authorsTogether = authorsTogether.concat(separator);
            }
        }
        return authorsTogether;
    }

    private void openSite(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            getContext().startActivity(intent);
        }
    }

}
