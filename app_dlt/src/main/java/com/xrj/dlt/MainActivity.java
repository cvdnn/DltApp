package com.xrj.dlt;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.xrj.dlt.Board.Levers;
import com.xrj.dlt.Board.Units;
import com.xrj.dlt.util.ThreadUtils;

import static com.xrj.dlt.Board.Levers.OFF;

public class MainActivity extends AppCompatActivity {

    private static final int TAG_UNIT = R.id.unit;
    private static final int TAG_LEVER = R.id.lever;

    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private LinearLayout mShellPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        mShellPanel = findViewById(R.id.shell);

        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);

        } else {
            onHandle();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                onHandle();
            }
        }
    }

    private void onHandle() {
        for (Units u : Units.values()) {
            mShellPanel.addView(newView(u));
        }
    }

    private Button newView(final Units u) {
        final Button btn = new Button(this);

        Levers levers = OFF;
        btn.setTag(TAG_UNIT, u);
        btn.setTag(TAG_LEVER, levers);

        if (u.readable) {
            levers = Board.Impl.getStatus(u);

        } else {
            Board.Impl.setStatus(u, levers);
        }

        final String LABEL = "%s(%s): %s";
        btn.setText(String.format(LABEL, u.name(), u.readable, levers.name()));
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Board.Units u = (Board.Units) v.getTag(TAG_UNIT);
                Board.Levers l = (Board.Levers) v.getTag(TAG_LEVER);

                if (u.readable) {
                    l = Board.Impl.getStatus(u);

                } else {
                    l = l.reverse();
                    Board.Impl.setStatus(u, l);
                }

                btn.setTag(TAG_LEVER, l);
                btn.setText(String.format(LABEL, u.name(), u.readable, l.name()));
            }
        });

        return btn;
    }

    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }
}
