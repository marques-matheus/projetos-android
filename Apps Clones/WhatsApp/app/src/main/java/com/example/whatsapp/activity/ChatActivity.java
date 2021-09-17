package com.example.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.adapter.MensagensAdapter;
import com.example.whatsapp.config.ConfiguracaoFirebase;
import com.example.whatsapp.helper.Base64Custom;
import com.example.whatsapp.helper.UsuarioFirebase;
import com.example.whatsapp.model.Conversa;
import com.example.whatsapp.model.Mensagem;
import com.example.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView textViewNome;
    private CircleImageView circleImageViewFoto;
    private Usuario usuarioDestinatario;
    private EditText editMensagem;

    //id usuarios remetente e destinatario
    private String idUsuarioRemetente;
    private String idUsuarioDestinatario;

    private RecyclerView recyclerMensagens;

    private MensagensAdapter adapter;
    private List<Mensagem> mensagens = new ArrayList<>();

    private DatabaseReference database;
    private DatabaseReference mensagensRef;
    private ChildEventListener childEventListenerMensagens;
    private StorageReference storage;

    private ImageView imageCamera;
    private static final int SELECAO_CAMERA = 100;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar( toolbar );


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //config iniciais

        recyclerMensagens = findViewById(R.id.recyclerMensagens);
        textViewNome = findViewById(R.id.textViewNomeChat);
        circleImageViewFoto = findViewById(R.id.circleImageFoto);
        editMensagem = findViewById(R.id.editMensagem);
        //recupera dados do remetente
        idUsuarioRemetente = UsuarioFirebase.getIdUsuario();
        imageCamera = findViewById(R.id.imageCamera);


        //recuperar dados do usuario destinatario

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            usuarioDestinatario = (Usuario) bundle.getSerializable("chatContato");
            textViewNome.setText(usuarioDestinatario.getNome());

            String foto = usuarioDestinatario.getFoto();
            if(foto != null){
                Uri url = Uri.parse(usuarioDestinatario.getFoto()) ;
                Glide.with(ChatActivity.this)
                        .load(url)
                        .into(circleImageViewFoto);


            }else {

                circleImageViewFoto.setImageResource(R.drawable.padrao);
            }

            //recupera dados destinatario
            idUsuarioDestinatario = Base64Custom.codificarBase64(usuarioDestinatario.getEmail());

        }

        //configurar adapter

        adapter = new MensagensAdapter(mensagens, getApplicationContext());


        //config recycler

       RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
       recyclerMensagens.setLayoutManager(layoutManager);
       recyclerMensagens.setHasFixedSize(true);
       recyclerMensagens.setAdapter(adapter);


        database = ConfiguracaoFirebase.getFirebaseDatabase();
        storage = ConfiguracaoFirebase.getFirebaseStorage();
        mensagensRef = database.child("mensagens")
                .child(idUsuarioRemetente)
                .child(idUsuarioDestinatario);




        //evento clique camera

        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(intent, SELECAO_CAMERA);

                }

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

          try {
              switch (requestCode){
                  case SELECAO_CAMERA:
                      imagem = (Bitmap) data.getExtras().get("data");
                      break;
              }
              if (imagem != null){
                  //recuperar dados da img para o firebase
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();

                  imagem.compress((Bitmap.CompressFormat.JPEG),100, baos);
                  byte[] dadosImagem = baos.toByteArray();

                  //nome da imagem

                  String nomeImagem = UUID.randomUUID().toString();

                  //configurar referencia FB

                 final StorageReference imagemRef = storage.child("imagens")
                          .child("fotos")
                          .child(idUsuarioRemetente)
                          .child(nomeImagem +"jpeg");

                  UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                  uploadTask.addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(Exception e) {

                          Log.d("Erro", "Erro ao fazer upload");

                          Toast.makeText(ChatActivity.this,
                                  "Erro ao fazer Upload da imagem",
                                  Toast.LENGTH_SHORT).show();

                      }
                  }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                      @Override
                      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                          imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                              @Override
                              public void onComplete(Task<Uri> task) {



                                  String downloadurl = task.getResult().toString();

                                  Mensagem mensagem = new Mensagem();
                                  mensagem.setIdUsuario(idUsuarioRemetente);
                                  mensagem.setMensagem("imagem.jpeg");
                                  mensagem.setImagem(downloadurl);

                                  salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
                                  salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);


                              }
                          });

                      }
                  });



              }

          }catch (Exception e){

              e.printStackTrace();


                 }
          }
    }

    public void enviarMensagem(View view){

        String textoMensagem = editMensagem.getText().toString();

        if (!textoMensagem.isEmpty()){

            Mensagem mensagem = new Mensagem();
            mensagem.setIdUsuario(idUsuarioRemetente);
            mensagem.setMensagem( textoMensagem );

            //salvar mensagem para remetente
            salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

            //salvar pra destinatario
            salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);

            //salvar conversa

            salvarConversa(mensagem);





        }else {
            Toast.makeText(ChatActivity.this,
                    "Digite uma mensagem para enivar!",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void salvarConversa(Mensagem msg){
        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdRemetente(idUsuarioRemetente);
        conversaRemetente.setIdDestinatario(idUsuarioDestinatario);
        conversaRemetente.setUltimaMensagem(msg.getMensagem());
        conversaRemetente.setUsuarioExibicao(usuarioDestinatario);

        conversaRemetente.salvar();
    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem msg){

        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference mensagemRef = database.child("mensagens");
        mensagemRef.child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(msg);


        //limpar texto

        editMensagem.setText("");



    }

    @Override
    protected void onStart() {
        super.onStart();

        recuperarMensagens();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagensRef.removeEventListener(childEventListenerMensagens);
    }

    public void recuperarMensagens(){

        childEventListenerMensagens = mensagensRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded( DataSnapshot snapshot,  String previousChildName) {
                Mensagem mensagem = snapshot.getValue(Mensagem.class);
                mensagens.add( mensagem );
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(  DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved( DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot,  String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


    }




}