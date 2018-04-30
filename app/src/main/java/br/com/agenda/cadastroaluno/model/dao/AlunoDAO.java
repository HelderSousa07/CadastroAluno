package br.com.agenda.cadastroaluno.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.List;

import br.com.agenda.cadastroaluno.model.bean.Aluno;

public class AlunoDAO extends SQLiteOpenHelper{

// CONSTANTES PARA AUXILIO NO CONTROLE DE VERSÕES
    private static final int VERSAO = 1;
    private static final String TABELA = "Aluno";
    private static final String DATABASE = "MPAlunos";

    //CONSTANTE PARA LOG NO LOGCAT
    private static final String TAG = "CADASTRO_ALUNO";

    public AlunoDAO(Context context){

        //Chamada do construtor que sabe acessar o DB
        super(context,DATABASE,null,VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //DEFINIÇÃO DO COMANDO DDL
        String ddl = "CREATE TABLE " + TABELA + "( " +
                "id INTEGER PRIMARY KEY," +
                "nome TEXT," +
                "telefone TEXT," +
                "endereco TEXT," +
                "site TEXT," +
                "email TEXT," +
                "foto TEXT," +
                "nota REAL)";

        //EXECUÇÃO DO COMANDO NO SQLITE
        db.execSQL(ddl);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //DEFINIÇÃO DO COMANDO PARA DESTRUIR A TABELA ALUNO
        String sql = "DROP TABLE IF EXISTS "+ TABELA;

        //EXECUÇÃO DO COMANDO DE DESTRUIÇÃO
        db.execSQL(sql);

        //CHAMADA AO MÉTODO DE CONSTRUÇÃO DA BASE DE DADOS
        onCreate(db);

    }

    public void cadastrar(Aluno aluno){

        //Objeto para armazenar os valores dos campos
        ContentValues values = new ContentValues();

        //Definição de valores dos campos da tabela;

        values.put("nome", aluno.getNome());
        values.put("telefone",aluno.getTelefone());
        values.put("endereco",aluno.getEndereco());
        values.put("site",aluno.getSite());
        values.put("email",aluno.getEmail());
        values.put("foto",aluno.getFoto());
        values.put("nota",aluno.getNota());

        //Inserir dados do aluno no DB
        getWritableDatabase().insert(TABELA,null,values);
        Log.i(TAG, "Aluno Cadastrado: "+aluno.getNome());

    }

    public void alterar(Aluno aluno){

        ContentValues values = new ContentValues();

        values.put("nome", aluno.getNome());
        values.put("telefone",aluno.getTelefone());
        values.put("endereco",aluno.getEndereco());
        values.put("site",aluno.getSite());
        values.put("email",aluno.getEmail());
        values.put("foto",aluno.getFoto());
        values.put("nota",aluno.getNota());

        String[] args = {aluno.getId().toString()};

        getWritableDatabase().update(TABELA, values, "id=?",args);
        Log.i(TAG, "Aluno Alterado: "+aluno.getNome());

    }

    public List<Aluno> listar(){

        //Definição da coleção de alunos
        List<Aluno> lista = new ArrayList<Aluno>();

        //Definição da instrução SQL
        String sql = "Select * from Aluno order by nome";

        //Objeto que recebe os registros do banco de dados
        Cursor cursor = getReadableDatabase().rawQuery(sql,null);

        try {
            while (cursor.moveToNext()){
                //Criação de nova referência
                Aluno aluno = new Aluno();

                //Carregar os atributos de Aluno com dados do DB
                aluno.setId(cursor.getLong(0));
                aluno.setNome(cursor.getString(1));
                aluno.setTelefone(cursor.getString(2));
                aluno.setEndereco(cursor.getString(3));
                aluno.setSite(cursor.getString(4));
                aluno.setEmail(cursor.getString(5));
                aluno.setFoto(cursor.getString(6));
                aluno.setNota(cursor.getDouble(7));

                //Adicionar novo aluno na lista
                lista.add(aluno);
            }
        } catch (SQLException e){
            Log.e(TAG, e.getMessage());
        } finally {
            cursor.close();
        }
        return lista;
    }

    public void excluir(Aluno aluno){

        //definição de array de parametros
        String[] args = {aluno.getId().toString()};

        //exclusão do Aluno
        getWritableDatabase().delete(TABELA,"id=?",args);

        Log.i(TAG,"Aluno deletado: "+aluno.getNome());

    }

}
