package br.com.agenda.cadastroaluno.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;

import br.com.agenda.cadastroaluno.FormularioActivity;
import br.com.agenda.cadastroaluno.R;
import br.com.agenda.cadastroaluno.model.bean.Aluno;

public class FormularioHelper {

    private EditText edtNome;
    private EditText edtTelefone;
    private EditText edtEndereco;
    private EditText edtSite;
    private EditText edtEmail;
    private SeekBar sbNota;
    private ImageView imgFoto;


    private Aluno aluno;

    FormularioActivity teste = new FormularioActivity();

    public FormularioHelper(FormularioActivity activity) {

        edtNome = (EditText) activity.findViewById(R.id.edtNome);
        edtEndereco = (EditText) activity.findViewById(R.id.edtEndereco);
        edtTelefone = (EditText) activity.findViewById(R.id.edtTelefone);
        edtEmail = (EditText) activity.findViewById(R.id.edtEmail);
        edtSite = (EditText) activity.findViewById(R.id.edtSite);
        sbNota = (SeekBar) activity.findViewById(R.id.sbNota);
        imgFoto = (ImageView) activity.findViewById(R.id.imgFoto);

        aluno = new Aluno();
    }

    public Aluno getAluno() {

        aluno.setNome(edtNome.getText().toString());
        aluno.setEmail(edtEmail.getText().toString());
        aluno.setEndereco(edtEndereco.getText().toString());
        aluno.setSite(edtSite.getText().toString());
        aluno.setTelefone(edtTelefone.getText().toString());
        aluno.setNota((double) sbNota.getProgress());

        return aluno;
    }

    public void setAluno(Aluno aluno){

        edtNome.setText(aluno.getNome());
        edtTelefone.setText(aluno.getTelefone());
        edtEndereco.setText(aluno.getEndereco());
        edtSite.setText(aluno.getSite());
        edtEmail.setText(aluno.getEmail());
        sbNota.setProgress(aluno.getNota().intValue());
      this.aluno = aluno;
        if(aluno.getFoto() != null){
            carregarFoto(aluno.getFoto());
        }

    }

    public ImageView getImgFoto() {
        return imgFoto;
    }

    public void carregarFoto(String localfoto){

     //   int targetFotoLargura = imgFoto.getWidth();
     //   int targetFotoAltura = imgFoto.getHeight();

     //   BitmapFactory.Options bmOptions = new BitmapFactory.Options();
      //  bmOptions.inJustDecodeBounds = true;

        //Carrega arquivo de imagem
     //   Bitmap imagemFoto = BitmapFactory.decodeFile(teste.getmImageFileLocation(),bmOptions);
     //   int cameraImageLargura = bmOptions.outWidth;
     //   int cameraImageAltura = bmOptions.outHeight;

      //  int scaleFactor = Math.min(cameraImageLargura/targetFotoLargura, cameraImageAltura/targetFotoAltura);
      //  bmOptions.inSampleSize = scaleFactor;
      //  bmOptions.inJustDecodeBounds = false;

        //Gerar imagem reduzida
      //  Bitmap imagemFotoReduzida = BitmapFactory.decodeFile(teste.getmImageFileLocation(),bmOptions);
        Bitmap bitmap = BitmapFactory.decodeFile(localfoto);
        imgFoto.setImageBitmap(bitmap);

        //guarda o caminho da foto do aluno
        aluno.setFoto(localfoto);

        //Atualiza a imagem exibida na tela de formulário
        imgFoto.setImageBitmap(bitmap);
    }



}
