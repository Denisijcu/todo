package todo.javatechig.com.tareas;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class FormLista extends AppCompatActivity {
    EditText e1, e2, e3, e4, e5, e6;
    DBAdapter myDB;
    Cursor fila;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_lista);

        e1 = (EditText)findViewById(R.id.e1);
        e2 = (EditText)findViewById(R.id.e2);
        e3 = (EditText)findViewById(R.id.e3);
        e4 = (EditText)findViewById(R.id.e4);
        e5 = (EditText)findViewById(R.id.e5);
        e6 = (EditText)findViewById(R.id.e6);

        OpenDB();
       fila= myDB.getAllRows();

        //fila=bd.rawQuery("select style, color, jugador, location, newlocation, total from transfs", null);
        if (fila.moveToFirst())
        {
            e1.setText(fila.getString(3));
            e2.setText(fila.getString(4));
            e3.setText(fila.getString(5));
            e4.setText(fila.getString(6));
            e5.setText(fila.getString(7));
            e6.setText(fila.getString(8));

        }
        else
        {
            e1.setText("No hay mas estilos");
        }










        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    private void OpenDB(){

        myDB = new DBAdapter(this);
        myDB.open();

    }
}
