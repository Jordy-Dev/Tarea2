package com.example.contactos;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText etNombre, etTelefono;
    Button btnAgregar;
    ListView listViewContactos;
    DBHelper dbHelper;
    ArrayList<Contacto> contactosList;
    ContactoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        btnAgregar = findViewById(R.id.btnAgregar);
        listViewContactos = findViewById(R.id.listViewContactos);

        dbHelper = new DBHelper(this);
        contactosList = new ArrayList<>();
        adapter = new ContactoAdapter(this, contactosList);
        listViewContactos.setAdapter(adapter);

        // Forzar el foco en el campo 'etNombre' para que el teclado aparezca
        etNombre.requestFocus();

        // Mostrar contactos al iniciar
        mostrarContactos();

        // Agregar o editar contacto
        btnAgregar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            String telefono = etTelefono.getText().toString();

            if (!nombre.isEmpty() && !telefono.isEmpty()) {
                // Si el botón muestra "Editar", se actualiza el contacto
                if (btnAgregar.getText().toString().equals("Editar Contacto ✏️")) {
                    dbHelper.updateContacto(getSelectedContactId(), nombre, telefono);
                    btnAgregar.setText("Agregar Contacto 💾");
                    Toast.makeText(MainActivity.this, "Contacto actualizado", Toast.LENGTH_SHORT).show();
                } else { // Si no, se agrega un nuevo contacto
                    dbHelper.addContacto(nombre, telefono);
                    Toast.makeText(MainActivity.this, "Contacto agregado", Toast.LENGTH_SHORT).show();
                }
                mostrarContactos();
                etNombre.setText("");
                etTelefono.setText("");
            } else {
                Toast.makeText(MainActivity.this, "Por favor ingresa nombre y teléfono", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para mostrar los contactos en la lista
    private void mostrarContactos() {
        contactosList.clear();
        Cursor cursor = dbHelper.getAllContactos();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(DBHelper.COL_1));
                String nombre = cursor.getString(cursor.getColumnIndex(DBHelper.COL_2));
                String telefono = cursor.getString(cursor.getColumnIndex(DBHelper.COL_3));
                contactosList.add(new Contacto(id, nombre, telefono));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    // Clase para representar un contacto
    public class Contacto {
        int id;
        String nombre;
        String telefono;

        public Contacto(int id, String nombre, String telefono) {
            this.id = id;
            this.nombre = nombre;
            this.telefono = telefono;
        }
    }

    // Adaptador personalizado para el ListView
    private class ContactoAdapter extends ArrayAdapter<Contacto> {

        public ContactoAdapter(MainActivity context, ArrayList<Contacto> contactos) {
            super(context, 0, contactos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_contacto, parent, false);
            }

            Contacto contacto = getItem(position);

            // Mostrar los datos del contacto en la vista
            TextView tvNombre = convertView.findViewById(R.id.tvNombre);
            TextView tvTelefono = convertView.findViewById(R.id.tvTelefono);
            Button btnEliminar = convertView.findViewById(R.id.btnEliminar);
            Button btnEditar = convertView.findViewById(R.id.btnEditar);

            tvNombre.setText(contacto.nombre);
            tvTelefono.setText(contacto.telefono);

            // Configurar el botón Eliminar
            btnEliminar.setOnClickListener(v -> {
                dbHelper.deleteContacto(contacto.id);
                mostrarContactos();
                Toast.makeText(MainActivity.this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
            });

            // Configurar el botón Editar
            btnEditar.setOnClickListener(v -> {
                // Rellenar los campos de edición con los datos del contacto seleccionado
                etNombre.setText(contacto.nombre);
                etTelefono.setText(contacto.telefono);

                // Cambiar el comportamiento del botón Agregar a "Editar"
                btnAgregar.setText("Editar Contacto ✏️");
                setSelectedContactId(contacto.id); // Guarda el ID del contacto a editar
            });

            return convertView;
        }
    }

    // Para manejar la ID del contacto seleccionado para edición
    private int selectedContactId = -1;

    private void setSelectedContactId(int id) {
        selectedContactId = id;
    }

    private int getSelectedContactId() {
        return selectedContactId;
    }
}
