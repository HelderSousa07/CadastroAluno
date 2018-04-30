package br.com.agenda.cadastroaluno;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

import br.com.agenda.cadastroaluno.model.bean.Aluno;
import br.com.agenda.cadastroaluno.model.dao.AlunoDAO;

public class ListaAlunosActivity extends AppCompatActivity {

    //definição de constantes
    private String TAG = "CADASTRO_ALUNO";
    private String ALUNOS_KEY = "LISTA";
    private static final int  MY_PERMISSION_REQUEST_SEND_SMS = 200;
    private String permissao;

    //atributos da tela

    private ListView lvwListagem;

    //colecao de alunos a ser exibida
    private List<Aluno> listaAlunos;

    //Arrayadapter converte listas ou vetores em view
    private ArrayAdapter<Aluno> adapter;
    //Difinição do layout de exibicao da listagem
    private int adapterLayout = android.R.layout.simple_list_item_1;

    private Aluno alunoSelecionado = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ligação da tela ao controlador
        setContentView(R.layout.lista_alunos_layout);
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //botão flutuante
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        lvwListagem = (ListView) findViewById(R.id.lvwListagem);

        //Informa que a ListView tem um menu de contexto
        registerForContextMenu(lvwListagem);

        //CLIQUE CURTO
        lvwListagem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent form = new Intent(ListaAlunosActivity.this,
                        FormularioActivity.class);

                alunoSelecionado = (Aluno) adapter.getItem(position);

                form.putExtra("ALUNO_SELECIONADO",alunoSelecionado);

                startActivity(form);

            }
        });
        //CLIQUE LONGO
        lvwListagem.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                //marca o aluno selecionado na listview
                alunoSelecionado = (Aluno) adapter.getItem(position);
                Log.i(TAG, "Aluno selecionado ListView.longClick()" +
                alunoSelecionado.getNome());

                return false;

            }
        });

    }

    private void excluirAluno(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirma a exclusão de: "+alunoSelecionado.getNome());

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
                dao.excluir(alunoSelecionado);
                dao.close();
                carregarLista();
                alunoSelecionado = null;
            }
        });

        builder.setNegativeButton("Não",null);
        AlertDialog dialog = builder.create();
        dialog.setTitle("Confirmação de Operação");
        dialog.show();

    }

    private void carregarLista(){

        //Criação do Objeto DAO - Inicio da conexao com DB
        AlunoDAO dao = new AlunoDAO(this);

        //Chamada ao metodo listar
        this.listaAlunos = dao.listar();

        //fim da conexao com DB
        dao.close();
        //ArrayAdapter sabe converter listas ou vetores em View
        this.adapter = new ArrayAdapter<Aluno>(this,adapterLayout,listaAlunos);
        this.lvwListagem.setAdapter(adapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        //carga da coleção Alunos
        this.carregarLista();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){

            case R.id.menu_novo:

                Intent intent = new Intent(ListaAlunosActivity.this,
                        FormularioActivity.class);
                startActivity(intent);

                return false;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.menu_contexto,menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Intent intent;

        switch (item.getItemId()){
            case R.id.menu_contexto_excluir:
                excluirAluno();
                break;
            case R.id.menu_contexto_ligar:
                intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+alunoSelecionado.getTelefone()));
                startActivity(intent);
                break;
            case R.id.menu_contexto_Enviar_Sms:

                pedirPermissaoParaSMS();

                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("sms:"+alunoSelecionado.getTelefone()));
                    intent.putExtra("sms_body","Seja Bem vindo "+alunoSelecionado.getNome());
                    startActivity(intent);

                //sendSMSMessage();
                break;
            case R.id.menu_contexto_Achar_no_mapa:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("geo:0,0?z=14&q="+alunoSelecionado.getEndereco()));
                intent.putExtra("sms_body", "Mensagem de Boas Vindas :)");
                startActivity(intent);
                break;
            case R.id.menu_contexto_navegar_site:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http:"+alunoSelecionado.getSite()));
                startActivity(intent);
                break;
            case R.id.menu_contexto_enviar_email:
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL,
                        new String[]{alunoSelecionado.getEmail()});
                intent.putExtra(Intent.EXTRA_SUBJECT,"Título");
                intent.putExtra(Intent.EXTRA_TEXT,"Conteúdo");
                startActivity(intent);
                break;

                default:
                    break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case MY_PERMISSION_REQUEST_SEND_SMS:
                if(grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED){

                     permissao = "concedida";

                } else {
                    permissao = "negada";
                }

                return;
        }

    }

    protected void sendSMSMessage(){

        Log.i("Send SMS: ","enviado");

        try {

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(alunoSelecionado.getTelefone(),null,"teste",null,null);
            Toast.makeText(this,"SMS enviado",Toast.LENGTH_LONG).show();

        }catch (Exception e){
            Toast.makeText(this,"Falha no envio", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    protected void pedirPermissaoParaSMS(){
        if(ContextCompat.checkSelfPermission(ListaAlunosActivity.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(ListaAlunosActivity.this,
                    new String[]{Manifest.permission.SEND_SMS},MY_PERMISSION_REQUEST_SEND_SMS);

        }
    }

}
