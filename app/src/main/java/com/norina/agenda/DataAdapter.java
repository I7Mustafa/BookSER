package com.norina.agenda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DataAdapter extends ArrayAdapter<Data>
{
    ImageView bookImage;
    TextView tvTitle, tvAuthor;

    public DataAdapter(Context context, int resource, ArrayList<Data> books)
    {
        super(context, resource, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if (view == null)
        {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item , parent,false);
        }
        Data data = getItem(position);

        bookImage = view.findViewById(R.id.bookImage);
        tvTitle = view.findViewById(R.id.bookTitle);
        tvAuthor = view.findViewById(R.id.bookAuthor);

        assert data != null;
        tvTitle.setText(data.getTitle());
        tvAuthor.setText(data.getAuthor());

        if (data.getImageUrl().length() != 0)
        {
            Picasso.get()
                    .load(data.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_notepad)
                    .resize(80, 80)
                    .into(bookImage);
        }
        else
            bookImage.setImageResource(R.drawable.ic_notepad);

        return view;
    }
}
