package id.co.viva.news.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import id.co.viva.news.app.R;

/**
 * Created by reza on 15/07/15.
 */
public class DetailPagingFragment extends Fragment {

    private String text;

    public static DetailPagingFragment newInstance(String textContent) {
        DetailPagingFragment detailPagingFragment = new DetailPagingFragment();
        Bundle bundle = new Bundle();
        bundle.putString("text_content", textContent);
        detailPagingFragment.setArguments(bundle);
        return detailPagingFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        text = getArguments().getString("text_content");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_paging_detail_article, container, false);
        TextView textContent = (TextView) rootView.findViewById(R.id.item_text_detail_article_paging);
        if (text != null) {
            if (text.length() > 0) {
                textContent.setText(text);
            }
        }
        return rootView;
    }

}
