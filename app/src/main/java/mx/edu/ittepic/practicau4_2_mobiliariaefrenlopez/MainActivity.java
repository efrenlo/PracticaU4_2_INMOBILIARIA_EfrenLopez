
package mx.edu.ittepic.practicau4_2_mobiliariaefrenlopez;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText identificacion,nombre,domicilio,telefono;
    Button insertar,consultar,eliminar,actualizar, mueble;
    BaseDatos base;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        identificacion=findViewById(R.id.editText);
        nombre=findViewById(R.id.editText2);
        domicilio=findViewById(R.id.editText3);
        telefono=findViewById(R.id.editText4);

        insertar=findViewById(R.id.button);
        consultar=findViewById(R.id.button2);
        eliminar=findViewById(R.id.button3);
        actualizar=findViewById(R.id.button4);
        mueble=findViewById(R.id.button5);

        //asignarle memoria y configuracion
        //cursosr, navegar entre los datos
        base = new BaseDatos(this,"inmobiliaria",null,1);

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codigoInsertar();
            }
        });

        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(1);
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actualizar.getText().toString().startsWith("CONFIRMAR ACTUALIZACION")){
                    invocarConfirmacionActualizacion();
                }else{
                    pedirID(2);
                }

            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(3);
            }
        });
        mueble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otra = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(otra);
            }
        });


    }

    private void invocarConfirmacionActualizacion() {
        AlertDialog.Builder confir = new AlertDialog.Builder(this);
        confir.setTitle("IMPORTNATE").setMessage("estas seguro que deseas aplicar cambios")
                .setPositiveButton("si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        aplicarActualizar();
                        dialog.dismiss();
                    }
                }).setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                habilitarBotonesYLimpiarCampos();
                dialog.cancel();
            }
        }).show();
    }
    private void habilitarBotonesYLimpiarCampos() {
        identificacion.setText("");
        nombre.setText("");
        domicilio.setText("");
        telefono.setText("");
        insertar.setEnabled(true);
        consultar.setEnabled(true);
        eliminar.setEnabled(true);
        actualizar.setText("ACTUALIZAR");
        identificacion.setEnabled(true);
    }

    private void aplicarActualizar() {
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();

            String SQL= "UPDATE PROPIETARIO SET NOMBRE='"+nombre.getText().toString()+"', DOMICILIO='"
                    +domicilio.getText().toString()+"', TELEFONO='"+telefono.getText().toString()+
                    "' WHERE IDP="+identificacion.getText().toString();
            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this,"SE actualizo",Toast.LENGTH_LONG).show();

        }catch (SQLiteException e){
            Toast.makeText(this,"No se pudo actualizar",Toast.LENGTH_LONG).show();
        }
        habilitarBotonesYLimpiarCampos();
    }

    private void pedirID(final int origen) {
        final EditText pidoID = new EditText(this);
        pidoID.setInputType(InputType.TYPE_CLASS_NUMBER);
        pidoID.setHint("Valor entero mayor de 0");
        String mensaje ="Escriba el id a buscar";

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);

        if(origen ==2){
            mensaje ="Ecriba el id a modificar";
        }
        if(origen ==3){
            mensaje ="Escriba que desea eliminar";
        }

        alerta.setTitle("atencion").setMessage(mensaje)
                .setView(pidoID)
                .setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(pidoID.getText().toString().isEmpty()){
                            Toast.makeText(MainActivity.this,"DEbes escribir un numero",Toast.LENGTH_LONG).show();
                            return;
                        }
                        buscarDato(pidoID.getText().toString(), origen);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancelar",null).show();

    }

    private void buscarDato(String idaBuscar, int origen){
        try{

            SQLiteDatabase tabla = base.getReadableDatabase();

            String SQL = "SELECT *FROM PROPIETARIO WHERE IDP="+idaBuscar;


            Cursor resultado = tabla.rawQuery(SQL,null);
            if(resultado.moveToFirst()){//mover le primer resultado obtenido de la consulta
                //si hay resulta´do
                if(origen==3){
                    //se consulto para borrar
                    String dato = idaBuscar+"&"+ resultado.getString(1)+"&"+resultado.getString(2)+
                            "&"+resultado.getString(3);
                    invocarConfirmacionEliminacion(dato);
                    return;
                }

                identificacion.setText(resultado.getString(0));
                nombre.setText(resultado.getString(1));
                domicilio.setText(resultado.getString(2));
                telefono.setText(resultado.getString(3));
                if(origen==2){
                    //modificar
                    insertar.setEnabled(false);
                    consultar.setEnabled(false);
                    eliminar.setEnabled(false);
                    actualizar.setText("CONFIRMAR ACTUALIZACION");
                    identificacion.setEnabled(false);
                }
            }else {
                //no hay resultado!
                Toast.makeText(this,"No se ENCONTRO EL RESULTADO",Toast.LENGTH_LONG).show();
            }
            tabla.close();

        }catch (SQLiteException e){
            Toast.makeText(this,"No se pudo buscar",Toast.LENGTH_LONG).show();
        }
    }

    private void invocarConfirmacionEliminacion(String dato) {
        String datos[] = dato.split("&");
        final String id = datos[0];
        String nombre = datos[1];

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("atencion").setMessage("Deseas eliminar al usuario: "+nombre)
                .setPositiveButton("Si a todo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        eliminarIdtodo(id);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancelar",null).show();
    }

    private void eliminarIdtodo(String id) {
        try{
            SQLiteDatabase tabla = base.getReadableDatabase();

            String SQL = "DELETE FROM PROPIETARIO WHERE IDP=" + id;
            tabla.execSQL(SQL);
            tabla.close();

            Toast.makeText(this, "SE elimino el dato", Toast.LENGTH_LONG).show();
        }catch (SQLiteException e){
            Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_LONG).show();
        }
    }

    private void codigoInsertar() {
        try {

            //metodo que compete a la inserccion,
            SQLiteDatabase tabla = base.getWritableDatabase();
                /*String SQL= "INSERT INTO PERSONA VALUES("+identificacion.getText().toString()+",'"+nombre.getText().toString()
                +"',"+edad.getText().toString()+",'"+genero.getText().toString()+"')";*/

            String SQL = "INSERT INTO PROPIETARIO VALUES(1,'%2','%3','%4')";
            SQL = SQL.replace("1", identificacion.getText().toString());
            SQL = SQL.replace("%2", nombre.getText().toString());
            SQL = SQL.replace("%3", domicilio.getText().toString());
            SQL = SQL.replace("%4", telefono.getText().toString());
            tabla.execSQL(SQL);
            identificacion.setText("");
            nombre.setText("");
            domicilio.setText("");
            telefono.setText("");

            Toast.makeText(this,"Si se pudo",Toast.LENGTH_LONG).show();
            tabla.close();

        }catch (SQLiteException e){

            Toast.makeText(this,"No se pudo",Toast.LENGTH_LONG).show();

        }
    }
}
