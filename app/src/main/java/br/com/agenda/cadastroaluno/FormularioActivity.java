package br.com.agenda.cadastroaluno;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.agenda.cadastroaluno.helper.FormularioHelper;
import br.com.agenda.cadastroaluno.model.bean.Aluno;
import br.com.agenda.cadastroaluno.model.dao.AlunoDAO;


public class FormularioActivity extends AppCompatActivity {

    private Button btnSalvar;
    private FormularioHelper helper;
    private Aluno alunoParaSerAlterado;
    private File localArquivo;
    private File arquivo;
    private Uri localFoto;
    private static final int FAZER_FOTO = 123;
    private static final int  MY_PERMISSION_REQUEST_CAMERA = 202;
    private static final int  MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 212;
    private String mImageFileLocation = "";

    public String getmImageFileLocation() {
        return mImageFileLocation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.formulario_layout);
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

        btnSalvar = (Button) findViewById(R.id.btnSalvarDados);
        helper = new FormularioHelper(this);

        alunoParaSerAlterado = (Aluno) getIntent()
                .getSerializableExtra("ALUNO_SELECIONADO");

        if(alunoParaSerAlterado!=null){
            helper.setAluno(alunoParaSerAlterado);
        }

        helper.getImgFoto().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               pedirPermissaoParaCamera();
                pedirPermissaoParaEscreverArquivo();


            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Utilizando do Helper para recuperar dados do Aluno
                Aluno aluno = helper.getAluno();

                //criação do objeto DAO - Inicio da conexão com o DB
                AlunoDAO dao = new AlunoDAO(FormularioActivity.this);

                //Verificação para Salvar ou cadastrar o aluno
                if(aluno.getId()==null){
                    dao.cadastrar(aluno);
                }else {
                    dao.alterar(aluno);
                }

                //Encerramento da conexão com o DB
                dao.close();

        //encerramento da Activity
        finish();

    }
});


    }

    public void chamarCamera(){


        Intent chamarCamera = new Intent();
        chamarCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File arquivoFoto = null;
        try {
            arquivoFoto = criarArquivoImagem();
        }catch (Exception e){
            e.printStackTrace();
        }

        String autorithies = getApplicationContext().getPackageName()+ ".fileprovider";
        Uri imagemUri = FileProvider.getUriForFile(FormularioActivity.this, autorithies
                ,arquivoFoto);
        chamarCamera.putExtra(MediaStore.EXTRA_OUTPUT,imagemUri);

        startActivityForResult(chamarCamera,FAZER_FOTO);
    }

    public File criarArquivoImagem() throws IOException{

        String timeStamp = DateFormat.getDateTimeInstance().toString();
        String nomeArquivo = "IMAGE_"+timeStamp+"_";
        File diretorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imagem = File.createTempFile(nomeArquivo,".jpg",diretorio);
        mImageFileLocation = imagem.getAbsolutePath();

        return imagem;

    }

    protected void pedirPermissaoParaCamera(){
        if(ContextCompat.checkSelfPermission(FormularioActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(FormularioActivity.this,
                    new String[]{Manifest.permission.CAMERA},MY_PERMISSION_REQUEST_CAMERA);

        }else{
            //chamarCamera();
        }
    }

    protected void pedirPermissaoParaEscreverArquivo(){
        if(ContextCompat.checkSelfPermission(FormularioActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(FormularioActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    ,MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);

        }else{
            chamarCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case MY_PERMISSION_REQUEST_CAMERA:{

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //chamarCamera();

                }

            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FAZER_FOTO){
            if (resultCode == RESULT_OK){
                helper.carregarFoto(mImageFileLocation);

            } else {
                localArquivo = null;
            }
        }

    }
}
