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
import java.util.Locale;

class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {

    private final List<Category> categoryList;
    private final Context context;

    CategoriesAdapter(Context context, List<Category> categoryList) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Category category = categoryList.get(position);

        Locale l = new Locale(category.getLanguage(), category.getCountry());

        String categoryName = category.getCategory();

        categoryName = categoryName.substring(0, 1).toUpperCase() + categoryName.substring(1);

        holder.category_category.setText(categoryName);
        holder.category_title.setText(category.getName());
        holder.category_country.setText(l.getDisplayCountry());
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView category_category,category_title, category_country;

        MyViewHolder(View view) {
            super(view);
            category_category = view.findViewById(R.id.cat_category);
            category_title = view.findViewById(R.id.category_title);
            category_country = view.findViewById(R.id.category_country);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            final int position = getAdapterPosition();

            if(context instanceof MainActivity){
                ((MainActivity)context).getTopHeadlines(categoryList.get(position).getId(), null);
            }

/*            Intent intent = new Intent(v.getContext(), categoryDetails.class);
            intent.putExtra("category", categoryList.get(position));
            v.getContext().startActivity(intent);*/
        }
    }
}
