package id.co.viva.news.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import id.co.viva.news.app.R;
import id.co.viva.news.app.model.RelatedArticle;

/**
 * Created by reza on 21/10/14.
 */
public class RelatedAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<RelatedArticle> relatedArticleArrayList;

    public RelatedAdapter(Context context, ArrayList<RelatedArticle> relatedArticleArrayList) {
        this.relatedArticleArrayList = relatedArticleArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return relatedArticleArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return relatedArticleArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.item_related_article, null);
            holder = new ViewHolder();
            holder.title_item_related = (TextView) view.findViewById(R.id.title_related_article);
            holder.date_item_related = (TextView) view.findViewById(R.id.date_related_article);

            RelatedArticle relatedArticle = relatedArticleArrayList.get(position);
            holder.title_item_related.setText(relatedArticle.getRelated_title());
            holder.date_item_related.setText(relatedArticle.getRelated_date_publish());

            view.setTag(holder);
        }
        return view;
    }

    private static class ViewHolder {
        private TextView title_item_related;
        private TextView date_item_related;
    }

}
