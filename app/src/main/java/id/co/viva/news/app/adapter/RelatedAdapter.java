package id.co.viva.news.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.item_related_article, viewGroup, false);

        TextView title_item_related = (TextView) rootView.findViewById(R.id.title_related_article);
        ImageView image_item_related = (ImageView) rootView.findViewById(R.id.image_related_article);

        RelatedArticle relatedArticle = relatedArticleArrayList.get(position);
        title_item_related.setText(relatedArticle.getRelated_title());
        if(relatedArticle.getImage().length() > 0) {
            Picasso.with(context).load(relatedArticle.getImage()).into(image_item_related);
        }
        return rootView;
    }

}
