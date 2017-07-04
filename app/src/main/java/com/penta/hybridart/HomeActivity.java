package com.penta.hybridart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.tv_basic_interaction)
    TextView tvBasicInteraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.tv_basic_interaction)
    public void gotoBasicInteractonActivity() {
        Intent intent = new Intent(this, BasicInteractionActivity.class);
        startActivity(intent);
    }
}
