package mx.edu.ittepic.practicau4_2_mobiliariaefrenlopez;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    EditText identificacion,domicilio,precioV,precioR,fecha,idp;
    Button insertar,consultar,eliminar,actualizar;
    Spinner spi;
    BaseDatos base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        identificacion=findViewById(R.id.editText);
        domicilio=findViewById(R.id.editText2);
        precioV=findViewById(R.id.editText3);
        precioR=findViewById(R.id.editText4);
        fecha=findViewById(R.id.editText5);
        idp=findViewById(R.id.editText6);

        insertar=findViewById(R.id.button);
        consultar=findViewById(R.id.button2);
        eliminar=findViewById(R.id.button3);
        actualizar=findViewById(R.id.button4);

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


    }

    private void invocarConfirmacionActualizacion() {
        AlertDialog.Builder confir = new AlertDialog.Builder(this);
        confir.setTitle("IMPORTANTE, PONER ATENCION").setMessage("estas Totalmente seguro que deseas aplicar cambios")
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
        domicilio.setText("");
        precioV.setText("");
        precioR.setText("");
        fecha.setText("");
        idp.setText("");

        insertar.setEnabled(true);
        consultar.setEnabled(true);
        eliminar.setEnabled(true);
        actualizar.setText("ACTUALIZAR");
        identificacion.setEnabled(true);
    }

    private void aplicarActualizar() {
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();

            String SQL= "UPDATE INMUEBLE SET DOMICILIO='"+domicilio.getText().toString()+"', PRECIOVENTA="
                    +precioV.getText().toString()+", PRECIORENTA="+precioR.getText().toString()+" ,FECHATRANSACCION='"+fecha.getText().toString()+
                    "' WHERE IDINMUEBLE="+identificacion.getText().toString();
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
            mensaje ="Escriba que desea eliminar este inmuebe";
        }

        alerta.setTitle("atencion").setMessage(mensaje)
                .setView(pidoID)
                .setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(pidoID.getText().toString().isEmpty()){
                            Toast.makeText(Main2Activity.this,"Debes escribir un numero",Toast.LENGTH_LONG).show();
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

            String SQL = "SELECT *FROM INMUEBLE WHERE IDINMUEBLE="+idaBuscar;

            Cursor resultado = tabla.rawQuery(SQL,null);
            if(resultado.moveToFirst()){ //mover le primer resultado obtenido de la consulta
                //si hay resulta´do
                if(origen==3){
                    //se consulto para borrar
                    String dato = idaBuscar+"&"+ resultado.getString(1)+"&"+resultado.getString(2)+
                            "&"+resultado.getString(3) +"&"+resultado.getString(4)+
                            "&"+resultado.getString(5);
                    invocarConfirmacionEliminacion(dato);
                    return;
                }

                identificacion.setText(resultado.getString(0));
                domicilio.setText(resultado.getString(1));
                precioV.setText(resultado.getString(2));
                precioR.setText(resultado.getString(3));
                fecha.setText(resultado.getString(4));
                idp.setText(resultado.getString(5));
                if(origen==2){
                    //modificar
                    insertar.setEnabled(false);
                    consultar.setEnabled(false);
                    eliminar.setEnabled(false);
                    actualizar.setText("CONFIRMAR ACTUALIZACION");
                    identificacion.setEnabled(false);
                    idp.setEnabled(false);
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
        alerta.setTitle("atencion").setMessage("Deseas eliminar el domicilio: "+nombre)
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

            String SQL = "DELETE FROM INMUEBLE WHERE IDINMUEBLE=" + id;
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
               /* String SQL= "INSERT INTO PERSONA VALUES("+identificacion.getText().toString()+",'"+nombre.getText().toString()
                +"',"+edad.getText().toString()+",'"+genero.getText().toString()+"')";*/

            String SQL = "INSERT INTO INMUEBLE VALUES(1,'%2',3,4,'%5',6)";
            SQL = SQL.replace("1", identificacion.getText().toString());
            SQL = SQL.replace("%2", domicilio.getText().toString());
            SQL = SQL.replace("3", precioV.getText().toString());
            SQL = SQL.replace("4", precioR.getText().toString());
            SQL = SQL.replace("%5", fecha.getText().toString());
            SQL = SQL.replace("6", idp.getText().toString());
            tabla.execSQL(SQL);

            Toast.makeText(this,"Si se pudo",Toast.LENGTH_LONG).show();
            tabla.close();

        }catch (SQLiteException e){

            Toast.makeText(this,"No se pudo",Toast.LENGTH_LONG).show();

        }
    }
}
