package id.co.viva.news.app.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.VivaApp;
import id.co.viva.news.app.interfaces.OnCompleteListener;
import id.co.viva.news.app.services.UserAccount;

/**
 * Created by reza on 03/12/14.
 */
public class ActComment extends FragmentActivity implements View.OnClickListener, OnCompleteListener {

    private String mImageUrl;
    private String mTitle;
    private String mIds;
    private TextView text_title;
    private ImageView image_content;
    private Button btnSubmit;
    private ListView listComment;
    private EditText etComment;
    private String fullname;
    private String email;
    private UserAccount userAccount;
    private boolean isInternetPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        mIds = bundle.getString("article_id");
        mImageUrl = bundle.getString("imageurl");
        mTitle = bundle.getString("title");

        setContentView(R.layout.act_comment);

        setHeader();
        getStateUser();

        text_title = (TextView)findViewById(R.id.text_title_content_coment);
        image_content = (ImageView)findViewById(R.id.img_thumb_content_comment);
        etComment = (EditText)findViewById(R.id.et_comment_user);
        listComment = (ListView)findViewById(R.id.list_comments);
        btnSubmit = (Button)findViewById(R.id.btn_send_comment);

        etComment.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        if(mTitle.length() > 0) {
            text_title.setText(mTitle);
        }

        if(mImageUrl.length() > 0) {
            Picasso.with(VivaApp.getInstance()).load(mImageUrl).into(image_content);
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

    private void setHeader() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setTitle("Komentar");
    }

    private void getStateUser() {
        VivaApp.getInstance().getDefaultEditor();
        fullname = VivaApp.getInstance().getSharedPreferences(VivaApp.getInstance())
                .getString(Constant.LOGIN_STATES_FULLNAME, "");
        email = VivaApp.getInstance().getSharedPreferences(VivaApp.getInstance())
                .getString(Constant.LOGIN_STATES_EMAIL, "");
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_send_comment) {
            String comments = etComment.getText().toString();
            if((fullname.length() == 0 && email.length() == 0) || (fullname.length() == 0) || (email.length() == 0)) {
                Toast.makeText(VivaApp.getInstance(), R.string.label_validation_for_comment, Toast.LENGTH_SHORT).show();
            } else if(comments.length() < 1) {
                Toast.makeText(VivaApp.getInstance(), R.string.label_validation_for_comment_length, Toast.LENGTH_SHORT).show();
            } else {
                if(isInternetPresent) {
                    userAccount = new UserAccount(mIds, email, fullname, comments, "Android");
                    userAccount.sendComment();
                }
            }
        } else if(view.getId() == R.id.et_comment_user) {
            if(fullname.length() == 0 && email.length() == 0) {
                Toast.makeText(VivaApp.getInstance(), R.string.label_validation_for_comment, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onComplete() {
        Toast.makeText(this, R.string.label_success_post_comment, Toast.LENGTH_SHORT).show();
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onFailed() {
        Toast.makeText(this, R.string.label_failed_post_comment, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {

    }

}
