package id.co.viva.news.app.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import id.co.viva.news.app.R;

/**
 * Created by reza on 31/12/14.
 */
public class ActDetailPhotoThumb extends FragmentActivity {

    private ImageView imageView;
    private String photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getHeaderActionBar();

        Bundle bundle = getIntent().getExtras();
        photoUrl = bundle.getString("photoUrl");
        setContentView(R.layout.act_detail_photo_thumb);

        imageView = (ImageView)findViewById(R.id.img_thumb_content_dialog);

        if(photoUrl.length() > 0) {
            Picasso.with(this).load(photoUrl).into(imageView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getHeaderActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setTitle("Detail Foto");
    }

}
