package uit.ensak.dish_wish_frontend.Authentification;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;

import uit.ensak.dish_wish_frontend.R;


public class page_acceuil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_acceuil);
    }
    public void getstarted(View view){
        startActivity(new Intent(page_acceuil.this, connect.class));

    }
}