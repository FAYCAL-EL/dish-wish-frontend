package uit.ensak.dish_wish_frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class terms extends AppCompatActivity {
    Button start;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        setContentView(R.layout.activity_terms);
        start= findViewById(R.id.st);
        back=findViewById(R.id.icon_24_bac);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent5= new Intent(terms.this, become_cook.class);
                startActivity(intent5);
            }
        });
    }
}