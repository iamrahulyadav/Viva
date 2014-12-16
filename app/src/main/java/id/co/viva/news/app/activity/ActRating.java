package id.co.viva.news.app.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.squareup.picasso.Picasso;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.interfaces.OnCompleteListener;
import id.co.viva.news.app.services.UserAccount;

/**
 * Created by reza on 05/12/14.
 */
public class ActRating extends FragmentActivity implements OnCompleteListener,
        View.OnClickListener, RatingBar.OnRatingBarChangeListener {

    private String mImageUrl;
    private String mTitle;
    private String mIds;
    private String mFromKanal;
    private ActionProcessButton btnRate;
    private String fullname;
    private String email;
    private String amountRate;
    private TextView text_title;
    private TextView text_amount_rate;
    private ImageView image_content;
    private UserAccount userAccount;
    private LinearLayout mLayout;
    private RatingBar ratingBar;
    private boolean isInternetPresent = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParameters();
        setContentView(R.layout.act_rating);
        getStateUser();

        isInternetPresent = Global.getInstance(this).getConnectionStatus().isConnectingToInternet();

        mLayout = (LinearLayout)findViewById(R.id.separator_line);
        text_title = (TextView)findViewById(R.id.text_title_content_rate);
        text_amount_rate = (TextView)findViewById(R.id.text_amount_rate);
        image_content = (ImageView)findViewById(R.id.img_thumb_content_rate);
        ratingBar = (RatingBar)findViewById(R.id.rating_article);
        btnRate = (ActionProcessButton)findViewById(R.id.btn_send_rate);
        btnRate.setMode(ActionProcessButton.Mode.ENDLESS);

        setThemes(mFromKanal);

        btnRate.setOnClickListener(this);
        ratingBar.setOnRatingBarChangeListener(this);

        if(mTitle.length() > 0) {
            text_title.setText(mTitle);
        }

        if(mImageUrl.length() > 0) {
            Picasso.with(this).load(mImageUrl).into(image_content);
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

    private void getParameters() {
        Bundle bundle = getIntent().getExtras();
        mIds = bundle.getString("article_id");
        mImageUrl = bundle.getString("imageurl");
        mTitle = bundle.getString("title");
        mFromKanal = bundle.getString("type_kanal");
    }

    private void getStateUser() {
        Global.getInstance(this).getDefaultEditor();
        fullname = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_FULLNAME, "");
        email = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_EMAIL, "");
    }

    private void setThemes(String kanal) {
        ColorDrawable colorDrawable = new ColorDrawable();
        if(kanal != null) {
            if(kanal.equalsIgnoreCase("bola")) {
                colorDrawable.setColor(getResources().getColor(R.color.color_bola));
                getActionBar().setBackgroundDrawable(colorDrawable);
                mLayout.setBackgroundResource(R.color.color_bola);
                btnRate.setBackgroundResource(R.color.color_bola);
            } else if(kanal.equalsIgnoreCase("vivalife")) {
                colorDrawable.setColor(getResources().getColor(R.color.color_life));
                getActionBar().setBackgroundDrawable(colorDrawable);
                mLayout.setBackgroundResource(R.color.color_life);
                btnRate.setBackgroundResource(R.color.color_life);
            } else {
                colorDrawable.setColor(getResources().getColor(R.color.color_news));
                getActionBar().setBackgroundDrawable(colorDrawable);
                mLayout.setBackgroundResource(R.color.color_news);
                btnRate.setBackgroundResource(R.color.color_news);
            }
        } else {
            colorDrawable.setColor(getResources().getColor(R.color.header_headline_terbaru_new));
            getActionBar().setBackgroundDrawable(colorDrawable);
            mLayout.setBackgroundResource(R.color.header_headline_terbaru_new);
            btnRate.setBackgroundResource(R.color.header_headline_terbaru_new);
        }
        getHeaderActionBar();
    }

    private void getHeaderActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setIcon(new ColorDrawable(getResources().
                getColor(android.R.color.transparent)));
        getActionBar().setTitle("Rate Artikel");
    }

    @Override
    public void onComplete(String message) {
        btnRate.setProgress(100);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onDelay(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed(String message) {
        enableView();
        btnRate.setProgress(0);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String message) {
        enableView();
        btnRate.setProgress(0);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_send_rate) {
            if(isInternetPresent) {
                if(amountRate != null) {
                    userAccount = new UserAccount(fullname, email, mIds, amountRate, this, ActRating.this);
                    disableView();
                    btnRate.setProgress(1);
                    userAccount.sendRating();
                } else {
                    Toast.makeText(this, R.string.title_validate_rate, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
        amountRate = String.valueOf(v);
        text_amount_rate.setText(amountRate);
    }

    private void disableView() {
        ratingBar.setEnabled(false);
        btnRate.setEnabled(false);
    }

    private void enableView() {
        ratingBar.setEnabled(true);
        btnRate.setEnabled(true);
    }

}
