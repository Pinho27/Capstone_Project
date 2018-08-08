package com.example.luis.capstoneproject;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

class HeadlinesAdapter extends RecyclerView.Adapter<HeadlinesAdapter.MyViewHolder> {

    private final List<Headline> headlineList;
    private final Context context;

    HeadlinesAdapter(Context context, List<Headline> headlinesList) {
        this.headlineList = headlinesList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.headline_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Headline headline = headlineList.get(position);

        final String completeDate = headline.getPublishedAt();
        String date = completeDate.substring(0, Math.min(completeDate.length(), 10));

        String sourceName = context.getResources().getString(R.string.by, headline.getSource().getName());

        holder.headline_category.setText(headline.getAuthor());
        holder.headline_title.setText(headline.getTitle());
        holder.headline_date.setText(date);
        holder.headline_source.setText(sourceName);


        if(headline.getUrlToImage() == null) {
            //Picasso.get().load(headline.urlToImage).into(holder.headline_image);
        }else
            Picasso.get().load(headline.getUrlToImage()).into(holder.headline_image);
    }

    @Override
    public int getItemCount() {
        return headlineList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView headline_category,headline_title, headline_date, headline_source ;
        final ImageView headline_image;

        MyViewHolder(View view) {
            super(view);
            headline_category = view.findViewById(R.id.category);
            headline_title = view.findViewById(R.id.title);
            headline_date = view.findViewById(R.id.date);
            headline_source = view.findViewById(R.id.source);

            headline_image = view.findViewById(R.id.image);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            final int position = getAdapterPosition();

            Intent intent = new Intent(v.getContext(), HeadlineDetails.class);
            intent.putExtra("headline", headlineList.get(position));
            v.getContext().startActivity(intent);
        }
    }
}
