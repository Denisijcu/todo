package todo.javatechig.com.tareas;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class Tareas extends AppCompatActivity {

    Time today = new Time(Time.getCurrentTimezone());
    DBAdapter myDB;
    EditText etTasks;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas);

        etTasks = (EditText)findViewById(R.id.etTasks);

        OpenDB();
        populateListView();
        ListViewItemClick();


    }

    private void OpenDB(){

        myDB = new DBAdapter(this);
        myDB.open();

    }

    public void onClick_AddTask(View view){
        today.setToNow();
        String timestamp = today.format("%y-%m-%d %H:%M:%S");



        if (!TextUtils.isEmpty(etTasks.getText().toString())){
            myDB.insertRow(etTasks.getText().toString(),timestamp);

        }
        etTasks.setText("");

        populateListView();



    }

    public void populateListView(){
        Cursor cursor = myDB.getAllRows();
        String[] fromFieldName = new String[] {DBAdapter.KEY_ROWID, DBAdapter.KEY_TASK};
        int[] toViewIDs = new int[] {R.id.itemId, R.id.itemTask};
        SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.activity_item, cursor,fromFieldName, toViewIDs,0 );

        ListView myList = (ListView) findViewById(R.id.ListViewTask);
        myList.setAdapter(myCursorAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tareas, menu);
        return true;
    }
    private void updateTask( long id){

        Cursor cursor =  myDB.getRow(id);
        if (cursor.moveToFirst()){
            String task = etTasks.getText().toString();



            today.setToNow();
            String date = today.format("%y-%m-%d %H:%M:%S");
            myDB.updateRow(id,task,date);
        }
        cursor.close();
    }

    private void ListViewItemClick(){

        ListView myList = (ListView) findViewById(R.id.ListViewTask);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateTask(id);
                populateListView();

            }
        });

    }
    public void DeleteAll(View view){
        myDB.deleteAll();
        populateListView();
    }

    public void ExportXLS(View view){

        exportToExcel(fetch());


    }

    public Cursor fetch() {
        String[] columns = new String[] { DBAdapter.KEY_ROWID, DBAdapter.KEY_TASK, DBAdapter.KEY_DATE};
        Cursor cursor = myDB.db.query(DBAdapter.DATABASE_TABLE, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    private void exportToExcel(Cursor cursor) {
        final String fileName = "TodoList.xls";

        //Saving file in external storage
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + "/javatechig.todo");



        //create directory if not exist
        if(!directory.isDirectory()){
            directory.mkdirs();
           // Toast.makeText(this,"Cree el directory"+directory,Toast.LENGTH_LONG).show();
        }

        //file path
        File file = new File(directory, fileName);

      // Toast.makeText(this,"El nombre del fichero es"+file,Toast.LENGTH_LONG).show();

        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook;
      //  WritableWorkbook workbook;
        //Toast.makeText(this,"Sali del SetLocale",Toast.LENGTH_SHORT).show();
        try {

        //   Toast.makeText(this,"sI ENTRO AQUI,, LO HIZO BIEN"+file+"/"+wbSettings,Toast.LENGTH_LONG).show();

            workbook=Workbook.createWorkbook(file, wbSettings);

          //  Toast.makeText(this,"Estoy creando el libro de trabajo con la config."+file+"/"+wbSettings,Toast.LENGTH_LONG).show();
            //Excel sheet name. 0 represents first sheet

            WritableSheet sheet = workbook.createSheet("MisTareas", 0);
            //Toast.makeText(this,"Creo los hojas de excel",Toast.LENGTH_SHORT).show();
            try {
                sheet.addCell(new Label(0, 0, "_id")); // column and row
                sheet.addCell(new Label(1, 0, "task"));
                sheet.addCell(new Label(2, 0, "fecha"));

                if (cursor.moveToFirst()) {
                    do {
                        String title = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_ROWID));
                        String desc = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_TASK));
                        String fech = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_DATE));
                        int i = cursor.getPosition() + 1;
                        sheet.addCell(new Label(0, i, title));
                        sheet.addCell(new Label(1, i, desc));
                        sheet.addCell(new Label(2, i, fech));
                    } while (cursor.moveToNext());
                }
               Toast.makeText(this,"La Exportacion al File XLS se realizo exitosamente. Nombre del archivo es Todolist.xls",Toast.LENGTH_LONG).show();
                //closing cursor
                cursor.close();
            } catch (RowsExceededException e) {
                e.printStackTrace();
                Toast.makeText(this,"Salio mal",Toast.LENGTH_LONG).show();
            } catch (WriteException e) {
                //Toast.makeText(this,"Es una pinga",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            //Toast.makeText(this,"Sali de los trys",Toast.LENGTH_SHORT).show();
            workbook.write();
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            //Toast.makeText(this,"Es una basura....."+directory+fileName,Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void Listado(View view){
        Intent iResult = new Intent(this,FormLista.class);
        startActivity(iResult);
    }
}
