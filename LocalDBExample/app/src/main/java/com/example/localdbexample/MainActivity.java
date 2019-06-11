package com.example.localdbexample;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView tvResult;
    private TextView tvFound;
    private EditText edtWrite;
    private Button btnSave;
    private Button btnRead;
    private Button btnFind;
    private DBOpenHelper dbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);
        tvFound = findViewById(R.id.tvFound);
        edtWrite = findViewById(R.id.edtWrite);
        btnSave = findViewById(R.id.btnSave);
        btnRead = findViewById(R.id.btnRead);
        btnFind = findViewById(R.id.btnFind);

        dbOpenHelper = new DBOpenHelper(this);

        dbOpenHelper.open();    // 이 부분을 매개변수로 DB_NAME을 넘겨서 원하는 파일을 열 수 있게 만든다.
        dbOpenHelper.create();  // open을 통해서 생성한 DB_NAME을 갖는 DB를 만든다.

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbOpenHelper.insertColumn(edtWrite.getText().toString(), " ", 0, " ");
                Toast.makeText(MainActivity.this, "insert", Toast.LENGTH_SHORT).show();
            }
        });

        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor c = dbOpenHelper.selectColumns();
                StringBuilder result = new StringBuilder();
                while(c.moveToNext()) {
                    result.append(c.getString(c.getColumnIndex("mytext"))).append("\n");
                }
                tvResult.setText(result);
                Toast.makeText(MainActivity.this, "db를 읽었다", Toast.LENGTH_SHORT).show();
            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor c = dbOpenHelper.selectColumns();
                String result = "결과를 찾지 못했습니다.";
                while(c.moveToNext()) {
                    if (edtWrite.getText().toString().equals(c.getString(c.getColumnIndex("mytext")))) {
                        result = c.getString(c.getColumnIndex("mytext"));
                    }
                }

                tvFound.setText(result);
            }
        });
    }
}
