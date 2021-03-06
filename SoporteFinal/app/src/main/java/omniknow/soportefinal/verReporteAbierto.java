package omniknow.soportefinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import omniknow.soportefinal.dao.impEvento;
import omniknow.soportefinal.dao.impUsuario;
import omniknow.soportefinal.extras.sUsuario;
import omniknow.soportefinal.modelo.Evento;
import omniknow.soportefinal.modelo.Usuario;

public class verReporteAbierto extends AppCompatActivity {

    private TextView txtUsuario;
    private TextView txtFecha;
    private TextView txtProblema;

    private Button btnAsignar;
    private Button btnResponder;
    private Button btnMantenimiento;

    private EditText txtSolucion;

    private Spinner comboInges;

    private int idEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_reporte_abierto);

        Intent int1 = getIntent();
        idEvento = Integer.parseInt(int1.getStringExtra("idEvento"));

        final impEvento impEvent = new impEvento(verReporteAbierto.this);
        final Evento event = impEvent.traeEvento(idEvento);

        impUsuario impUser = new impUsuario(verReporteAbierto.this);
        Usuario user = impUser.traeUsuario(event.getIdUsuario());


        txtUsuario = (TextView)findViewById(R.id.txtUser);
        txtFecha = (TextView)findViewById(R.id.txtFecha);
        txtProblema = (TextView)findViewById(R.id.txtProblema);

        btnAsignar = (Button)findViewById(R.id.btnAsignar);
        btnResponder = (Button)findViewById(R.id.btnResponder);
        btnMantenimiento = (Button)findViewById(R.id.btnMantenimiento);

        txtSolucion = (EditText)findViewById(R.id.txtSolucion);

        comboInges = (Spinner)findViewById(R.id.comboInges);


        //ASIGNO LOS VALORES A LAS VARIABLES
        String[] inges = {"inge1", "inge2", "inge3"};
        comboInges.setAdapter(new ArrayAdapter<String>(verReporteAbierto.this, android.R.layout.simple_spinner_item, inges));

        txtUsuario.setText(user.getNombre() + "");
        txtProblema.setText(event.getProblema() + "");
        txtFecha.setText(event.getFecha() + "");

        //PONGO LAS COSAS A LOS BOTONES

        btnAsignar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                impEvento impEvent = new impEvento(verReporteAbierto.this);
                String res = impEvent.asignar(comboInges.getSelectedItemPosition() + 3,idEvento);
                Toast.makeText(verReporteAbierto.this,res,Toast.LENGTH_SHORT).show();
                if (sUsuario.tipo == 3) {
                    Intent int1 = new Intent(verReporteAbierto.this, verGerente.class);
                    startActivity(int1);
                }else if (sUsuario.tipo == 2){
                    Intent int1 = new Intent(verReporteAbierto.this, verInge.class);
                    startActivity(int1);
                }
            }
        });

        btnResponder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                impEvento impEvent = new impEvento(verReporteAbierto.this);
                String res = impEvent.responder(txtSolucion.getText().toString(),idEvento);
                Toast.makeText(verReporteAbierto.this,res,Toast.LENGTH_SHORT).show();
                if (sUsuario.tipo == 3) {
                    Intent int1 = new Intent(verReporteAbierto.this, verGerente.class);
                    startActivity(int1);
                }else if (sUsuario.tipo == 2){
                    Intent int1 = new Intent(verReporteAbierto.this, verInge.class);
                    startActivity(int1);
                }
            }
        });

        btnMantenimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                impEvento impEvent = new impEvento(verReporteAbierto.this);
                String res = impEvent.enviarMantenimiento(idEvento);
                Toast.makeText(verReporteAbierto.this,res,Toast.LENGTH_SHORT).show();
                if (sUsuario.tipo == 3) {
                    Intent int1 = new Intent(verReporteAbierto.this, verGerente.class);
                    startActivity(int1);
                }else if (sUsuario.tipo == 2){
                    Intent int1 = new Intent(verReporteAbierto.this, verInge.class);
                    startActivity(int1);
                }
            }
        });



    }
}
