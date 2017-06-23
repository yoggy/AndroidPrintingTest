package net.sabamiso.android.androidprintingtest;

import android.content.Intent;
import android.print.PrintManager;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public final String TAG = getClass().getSimpleName();

    Button buttonPrintTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPrintTest = (Button)findViewById(R.id.buttonPrintTest);
        buttonPrintTest.setAllCaps(false);
        buttonPrintTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPrintTest();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
    }

    private void onPrintTest(){
        if (PrintHelper.systemSupportsPrint() == true) {
            TestPrintAdapter adapter = new TestPrintAdapter(this);
            PrintManager printManager =
                    (PrintManager) getSystemService(PRINT_SERVICE);

            String jobName = getString(R.string.app_name) + "の印刷";
            printManager.print(jobName, adapter, null);
        }
        else {
            Toast.makeText(this, "PrintHelper.systemSupportsPrint() == false", Toast.LENGTH_LONG).show();
        }
    }
}
