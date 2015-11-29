package chanh.androidlab4;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Database 관련 객체들
    SQLiteDatabase db;
    String dbName = "idList.db"; // name of Database;
    String tableName = "idListTable"; // name of Table;
    int dbMode = Context.MODE_PRIVATE;

    // layout object
    EditText mEtName;
    EditText mEtID;
    Button mBtInsert;
    Button mBtRead;
    Button mBtDelete;
    Button mBtUpdate;
    Button mBtReset;
    Button mBtSelect;

    ListView mList;
    ArrayAdapter<String> baseAdapter;
    ArrayList<String> nameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // // Database 생성 및 열기
        db = openOrCreateDatabase(dbName, dbMode, null);
        // 테이블 생성
        createTable();

        mEtName = (EditText) findViewById(R.id.et_text);
        mEtID = (EditText) findViewById(R.id.et_Id);
        mBtInsert = (Button) findViewById(R.id.bt_insert);
        mBtRead = (Button) findViewById(R.id.bt_read);
        mBtDelete = (Button) findViewById(R.id.bt_delete);
        mBtUpdate = (Button) findViewById(R.id.bt_update);
        mBtReset = (Button) findViewById(R.id.bt_reset);
        mBtSelect = (Button) findViewById(R.id.bt_select);
        ListView mList = (ListView) findViewById(R.id.list_view);

        mBtInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEtName.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "TEXT 입력하세요", Toast.LENGTH_SHORT).show();
                else {
                    String name = mEtName.getText().toString();
                    insertData(name);
                }
            }
        });

        mBtRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameList.clear();
                selectAll();
                baseAdapter.notifyDataSetChanged();
            }
        });

        // Create listview
        nameList = new ArrayList<String>();
        baseAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, nameList);
        mList.setAdapter(baseAdapter);

        mBtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEtID.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "ID 입력하세요", Toast.LENGTH_SHORT).show();
                else {
                    String id = mEtID.getText().toString();
                    removeData(Integer.parseInt(id));
                }
            }
        });

        mBtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEtID.getText().toString().equals("") && mEtName.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "다시 입력하세요", Toast.LENGTH_SHORT).show();
                else if (mEtName.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "TEXT 입력하세요", Toast.LENGTH_SHORT).show();
                else if (mEtID.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "ID 입력하세요", Toast.LENGTH_SHORT).show();
                else {
                    String id = mEtID.getText().toString();
                    String name = mEtName.getText().toString();
                    updateData(Integer.parseInt(id), name);
                }
            }
        });

        mBtReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTable();
                createTable();
            }
        });

        mBtSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEtID.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "ID 입력하세요", Toast.LENGTH_SHORT).show();
                else {
                    String id = mEtID.getText().toString();
                    selectData(Integer.parseInt(id));
                }
            }
        });
    }

    // Table 생성
    public void createTable() {
        try {
            String sql = "create table " + tableName + "(id integer primary key autoincrement, " + "name text not null)";
            db.execSQL(sql);
        } catch (android.database.sqlite.SQLiteException e) {
            Log.d("Lab sqlite", "error: " + e);
        }
    }

    // Table 삭제
    public void removeTable() {
        String sql = "drop table " + tableName;
        db.execSQL(sql);
    }

    // Data 추가
    public void insertData(String name) {
        String sql = "insert into " + tableName + " values(NULL, '" + name + "');";
        db.execSQL(sql);
    }

    // Data 업데이트
    public void updateData(int index, String name) {
        String sql = "update " + tableName + " set name = '" + name + "' where id = " + index + ";";
        db.execSQL(sql);
    }

    // Data 삭제
    public void removeData(int index) {
        String sql = "select * from " + tableName + " where id = " + index + ";";
        Cursor result = db.rawQuery(sql, null);

        if (result.moveToFirst()) {
            sql = "delete from " + tableName + " where id = " + index + ";";
            db.execSQL(sql);
            Toast.makeText(this, "삭제했습니다", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "비어있습니다", Toast.LENGTH_SHORT).show();
        }
    }

    // Data 읽기(꺼내오기)
    public void selectData(int index) {
        String sql = "select * from " + tableName + " where id = " + index + ";";
        Cursor result = db.rawQuery(sql, null);

        // result(Cursor 객체)가 비어 있으면 false 리턴
        if (result.moveToFirst()) {
            int id = result.getInt(0);
            String name = result.getString(1);
            Toast.makeText(this, "index= " + id + " name=" + name, Toast.LENGTH_SHORT).show();
            Log.d("lab_sqlite", "\"index= \" + id + \" name=\" + name ");
        } else {
            Toast.makeText(this, "비어있습니다", Toast.LENGTH_SHORT).show();
        }
        result.close();
    }

    // 모든 Data 읽기
    public void selectAll() {
        String sql = "select * from " + tableName + ";";
        Cursor results = db.rawQuery(sql, null);
        results.moveToFirst();

        while (!results.isAfterLast()) {
            int id = results.getInt(0);
            String name = results.getString(1);
//            Toast.makeText(this, "index= " + id + " name=" + name, Toast.LENGTH_SHORT).show();
            Log.d("lab_sqlite", "index= " + id + " name=" + name);

            nameList.add(name);
            results.moveToNext();
        }
        results.close();
    }
}