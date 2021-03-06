package com.example.jorgeantoniojuarezleyva.reportedeeventos;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.example.jorgeantoniojuarezleyva.reportedeeventos.ImpDAO.impEvento;
import com.example.jorgeantoniojuarezleyva.reportedeeventos.ImpDAO.impUsuario;
import com.example.jorgeantoniojuarezleyva.reportedeeventos.Modelo.Evento;
import com.example.jorgeantoniojuarezleyva.reportedeeventos.dao.ReporteMantenimientoDao;
import com.example.jorgeantoniojuarezleyva.reportedeeventos.dao.EstadoReporteDao;
import com.example.jorgeantoniojuarezleyva.reportedeeventos.dao.TipoMantenimientoDao;
import com.example.jorgeantoniojuarezleyva.reportedeeventos.dao.UsuarioDao;
import com.example.jorgeantoniojuarezleyva.reportedeeventos.extras.Constante;
import com.example.jorgeantoniojuarezleyva.reportedeeventos.Modelo.EstadoReporte;
import com.example.jorgeantoniojuarezleyva.reportedeeventos.Modelo.TipoMantenimiento;
import com.example.jorgeantoniojuarezleyva.reportedeeventos.Modelo.Usuario;

public class EditarReporte extends AppCompatActivity {
    Spinner sEstado, sTipo, sProgramador;
    EditText etAsunto, etDescripcion, etSolucion, etHoras;
    Button bGuardar;
    TextView tvProgramador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_reporte);

        sEstado = (Spinner) findViewById(R.id.sEstado);
        sTipo = (Spinner) findViewById(R.id.sTipo);
        sProgramador = (Spinner) findViewById(R.id.sProgramador);
        etAsunto = (EditText) findViewById(R.id.etAsunto);
        etDescripcion = (EditText) findViewById(R.id.etDescripcion);
        etSolucion = (EditText) findViewById(R.id.etSolucion);
        etHoras = (EditText) findViewById(R.id.etHoras);
        bGuardar = (Button) findViewById(R.id.bGuardar);
        tvProgramador = (TextView) findViewById(R.id.tvProgramador);

        cargarSpinner(sEstado, 0);
        cargarSpinner(sTipo, 1);
        cargarSpinner(sProgramador, 2);

        if(Constante.usuarioActual.getTipo() != 5) { // Si el usuario no es Gerente de mantenimiento
            tvProgramador.setVisibility(View.GONE);
            sProgramador.setVisibility(View.GONE);
        } else { // Se aplica cuando es el Gerente de Mantenimiento
//            etSolucion.setEnabled(false);
//            sEstado.setEnabled(false);
        }

        sEstado.setSelection(Constante.reporteActual.getIdEstadoReporte() - 1);
        sTipo.setSelection(Constante.reporteActual.getIdTipoMantenimiento() - 1);
        etAsunto.setText(Constante.reporteActual.getAsunto());
        etDescripcion.setText(Constante.reporteActual.getDescripcion());
        etSolucion.setText(Constante.reporteActual.getSolucion());
        etHoras.setText(String.valueOf(Constante.reporteActual.getHoras()));

        UsuarioDao usuarioDao = new UsuarioDao();
        String myString = "";
        if(Constante.reporteActual.getIdUsuario() != 0)
            myString = usuarioDao.consulta(this, Constante.reporteActual.getIdUsuario()).getNombre(); //the value you want the position for
            ArrayAdapter myAdap = (ArrayAdapter) sProgramador.getAdapter(); //cast to an ArrayAdapter
            sProgramador.setSelection(myAdap.getPosition(myString));
    }

    public void cargarSpinner(Spinner spinner, int tipo) {
        EstadoReporteDao estadoReporteDao = new EstadoReporteDao();
        TipoMantenimientoDao tipoMantenimientoDao = new TipoMantenimientoDao();
        UsuarioDao usuarioDao = new UsuarioDao();

        List<EstadoReporte> estados;
        List<TipoMantenimiento> tipos;
        List<Usuario> usuarios;
        List<String> etiquetas = new ArrayList<>();
        if(tipo == 0) {
            estados = estadoReporteDao.ver(this);
            if(Constante.reporteActual.isEvento()) {
                for (EstadoReporte i : estados)
                    if (i.getIdEstadoReporte() < 3) // Sólo se puede marcar hasta resuelto
                        etiquetas.add(i.getNombre());
            }
            else { // No es de evento
                for (EstadoReporte i : estados)
                    if (i.getIdEstadoReporte() < 4) // Se puede marcar hasta cerrado
                        etiquetas.add(i.getNombre());
            }
        }
        else if(tipo == 1) {
            tipos = tipoMantenimientoDao.ver(this);
            for(TipoMantenimiento i : tipos)
                etiquetas.add(i.getNombre());
        }
        else if(tipo == 2) {
            usuarios = usuarioDao.ver(this);
            etiquetas.add("Ninguno");
            for(Usuario i : usuarios)
                if(i.getTipo() == 6) // Si es un programador
                    etiquetas.add(i.getNombre());
        }
        else
            System.out.println("\n\nError al cargar spinners\n\n");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, etiquetas);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    public void guardar(View v) {
        int idEstadoReporte = sEstado.getSelectedItemPosition() + 1;
        Constante.reporteActual.setIdEstadoReporte(idEstadoReporte);
        String descripcion = etDescripcion.getText().toString();
        String solucion = etSolucion.getText().toString();

        if(descripcion.equals("")) // Si la descripción está vacía
            Toast.makeText(EditarReporte.this, "Falta una descripción del problema", Toast.LENGTH_LONG).show();
        else if(solucion.equals("")) // Si la solución está vacía
            if(idEstadoReporte == 1) { //Se marca como pendiente
                darDeAlta();
            }
            else { // Se marca como resuelta
                Toast.makeText(EditarReporte.this, "No hay solución para el reporte resuelto", Toast.LENGTH_LONG).show();
            }
        else { // Hay descripción y hay solución
            if(idEstadoReporte == 1) { // Se marca como pendiente
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Editar reporte")
                        .setMessage("El reporte será guardado como \"Resuelto\" porque tiene una solución.")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Constante.reporteActual.setIdEstadoReporte(2); // Estado de "Resuelto"
                                darDeAlta();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
            else { // Se marca como resuelto
                darDeAlta();
            }
        }
    }

    public void darDeAlta() {
        final ReporteMantenimientoDao reporteMantenimientoDao = new ReporteMantenimientoDao();
        UsuarioDao usuarioDao = new UsuarioDao();

        String asunto = etAsunto.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String solucion = etSolucion.getText().toString();
        int horas = 0;
        if(!etHoras.getText().toString().equals(""))
            horas = Integer.parseInt(etHoras.getText().toString());
        int idUsuario = 0;
        if(Constante.usuarioActual.getTipo() == 5) {// Es gerente
            if (sProgramador.getSelectedItemPosition() != 0)
                idUsuario = usuarioDao.buscaPorNombre(this, sProgramador.getSelectedItem().toString()).getIdUsuario();
        } else if(Constante.usuarioActual.getTipo() == 6) // Si el usuario es programador, automáticamente se le asignará el reporte que genere
            idUsuario = Constante.usuarioActual.getIdUsuario();

        Constante.reporteActual.setAsunto(asunto);
        Constante.reporteActual.setDescripcion(descripcion);
        Constante.reporteActual.setSolucion(solucion);
        Constante.reporteActual.setHoras(horas);
        Constante.reporteActual.setIdUsuario(idUsuario);

        reporteMantenimientoDao.cambio(EditarReporte.this, Constante.reporteActual); // Actualizar en mantenimiento

        // Actualizar en eventos
        if(Constante.reporteActual.isEvento()) {
            impEvento impEvent = new impEvento(this);
            impUsuario impUser = new impUsuario(this);

            Evento evento = impEvent.traeEvento(Constante.reporteActual.getIdReporteEvento());
            evento.setEstado("SOLUCIONADO");

            Usuario usuario = impUser.traeUsuario(evento.getIdUsuario());
            impEvent.cambiarReporte(evento, usuario);
        }
        Toast.makeText(EditarReporte.this, "Reporte actualizado", Toast.LENGTH_LONG).show();
        Intent i = new Intent(EditarReporte.this, VerReporte.class);
        finish();
        startActivity(i);
    }
}
