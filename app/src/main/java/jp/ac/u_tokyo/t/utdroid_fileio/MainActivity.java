package jp.ac.u_tokyo.t.utdroid_fileio;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    /* Viewを格納する変数 */
    private EditText editTextDevice;
    private EditText editTextSdcard;
    private EditText editTextSharedPrefs;

    /* Key-Valueストアを利用するための変数 */
    private SharedPreferences sharedPreferences;
    /* データを格納する際と、取り出す際に必要となる、番号札のようなもの */
    private final String PREF_KEY = "mydata";

    /* ファイルに書き出す際のファイル名 */
    private final String FILE_NAME = "myfile.txt";

    /* SDカードにアクセスするPermission取得のための定数 */
    private static final int SD_ACCESS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* それぞれの名前に対応するViewを取得する */
        editTextDevice = (EditText) findViewById(R.id.editTextDevice);
        editTextSdcard = (EditText) findViewById(R.id.editTextSdcard);
        editTextSharedPrefs = (EditText) findViewById(R.id.editTextSharedPrefs);

        /* SharedPreferencesの準備 */
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        /**
         * 本体メモリの場合
         */
        findViewById(R.id.buttonDeviceLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 読み込んだデータを一時的に蓄えるためのバッファ */
                StringBuffer sb = new StringBuffer();

                /* 実際の読み込み処理 */
                try {
                    /* テキストファイルの場合のお作法。バイナリファイルの場合は扱いが異なる */
                    FileInputStream fis = openFileInput(FILE_NAME);
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader br = new BufferedReader(isr);
                    /* 1行ずつ読み込む */
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                        sb.append(System.getProperty("line.separator"));
                    }
                    br.close();

                    Toast.makeText(getBaseContext(), "本体メモリから読み込みました。", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /* 入力欄に読み込んだデータを表示する */
                editTextDevice.setText(sb.toString());
            }
        });

        findViewById(R.id.buttonDeviceSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 入力欄からテキストを取得 */
                String text = editTextDevice.getText().toString();

                /* 実際の保存処理 */
                try {
                    /* テキストファイルの場合のお作法。バイナリファイルの場合は扱いが異なる */
                    FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fos);
                    BufferedWriter bw = new BufferedWriter(osw);
                    bw.write(text);
                    bw.flush();
                    bw.close();

                    Toast.makeText(getBaseContext(), "本体メモリに保存しました。", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.buttonDeviceDel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 入力欄のテキストを消去 */
                editTextDevice.setText("");
            }
        });

        /**
         * SDカードの場合
         */
        findViewById(R.id.buttonSdcardLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* SDカードが利用可能かチェック */
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(getBaseContext(), "SDカードが利用できません。", Toast.LENGTH_SHORT).show();
                } else {
                    /* 読み込んだデータを一時的に蓄えるためのバッファ */
                    StringBuffer sb = new StringBuffer();

                    /* 実際の読み込み処理 */
                    try {
                        /* ファイルの絶対パスを取得 */
                        String filePath = Environment.getExternalStorageDirectory() + "/" + FILE_NAME;

                        /* 1行目だけが本体メモリの場合と異なる */
                        FileInputStream fis = new FileInputStream(filePath);
                        InputStreamReader isr = new InputStreamReader(fis);
                        BufferedReader br = new BufferedReader(isr);

                        /* 1行ずつ読み込む */
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                            sb.append(System.getProperty("line.separator"));
                        }
                        br.close();

                        Toast.makeText(getBaseContext(), "SDカードから読み込みました。", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    /* 入力欄に読み込んだデータを表示する */
                    editTextSdcard.setText(sb.toString());
                }
            }
        });

        findViewById(R.id.buttonSdcardSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* SDカードが利用可能かチェック */
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(getBaseContext(), "SDカードが利用できません。", Toast.LENGTH_SHORT).show();
                } else {
                    /* 入力欄からテキストを取得 */
                    String text = editTextSdcard.getText().toString();

                    /* 実際の保存処理 */
                    try {
                        /* ファイルの絶対パスを取得 */
                        String filePath = Environment.getExternalStorageDirectory() + "/" + FILE_NAME;

                        /* 1行目だけが本体メモリの場合と異なる */
                        FileOutputStream fos = new FileOutputStream(filePath);
                        OutputStreamWriter osw = new OutputStreamWriter(fos);
                        BufferedWriter bw = new BufferedWriter(osw);
                        bw.write(text);
                        bw.flush();
                        bw.close();

                        Toast.makeText(getBaseContext(), "SDカードに保存しました。", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        findViewById(R.id.buttonSdcardDel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 入力欄のテキストを消去 */
                editTextSdcard.setText("");
            }
        });

        /**
         * SharedPreferencesの場合
         */
        findViewById(R.id.buttonSharedPrefsLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 実際の読み込み処理。Keyとデフォルト値を渡す */
                String data = sharedPreferences.getString(PREF_KEY, "");

                /* 入力欄に読み込んだデータを表示する */
                editTextSharedPrefs.setText(data);

                Toast.makeText(getBaseContext(), "SharedPreferencesから読み込みました。", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.buttonSharedPrefsSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 入力欄からテキストを取得 */
                String text = editTextSharedPrefs.getText().toString();

                /* 実際の保存処理。KeyとValueをセットで渡す。commitしないと保存されないので注意 */
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PREF_KEY, text);
                editor.commit();

                Toast.makeText(getBaseContext(), "SharedPreferencesに保存しました。", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.buttonSharedPrefsDel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 入力欄のテキストを消去 */
                editTextSharedPrefs.setText("");
            }
        });

        /* Android 6.0以上かどうかで条件分岐 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /* Permissionを取得済みかどうか確認 */
            String[] dangerousPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                /* 未取得ならPermissionを要求 */
                requestPermissions(dangerousPermissions, SD_ACCESS_REQUEST_CODE);
            }else{
                Toast.makeText(this, "SDカードにアクセスできます。", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "SDカードにアクセスできます。", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Android 6.0以上のDANGEROUS_PERMISSION対策
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == SD_ACCESS_REQUEST_CODE) {
            // Permissionが許可された
            if (grantResults.length == 0) {
                return;
            }else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SDカードにアクセスできます。", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SDカードへのアクセスを許可して下さい。", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
