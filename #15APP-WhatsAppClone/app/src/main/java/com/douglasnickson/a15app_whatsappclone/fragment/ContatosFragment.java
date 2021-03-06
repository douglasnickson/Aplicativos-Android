package com.douglasnickson.a15app_whatsappclone.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.douglasnickson.a15app_whatsappclone.R;
import com.douglasnickson.a15app_whatsappclone.activity.ConversaActivity;
import com.douglasnickson.a15app_whatsappclone.adapter.ContatoAdapter;
import com.douglasnickson.a15app_whatsappclone.config.ConfiguracaoFirebase;
import com.douglasnickson.a15app_whatsappclone.helper.Preferencias;
import com.douglasnickson.a15app_whatsappclone.model.Contato;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    //private ArrayList<String> contatos;
    private ArrayList<Contato> contatos;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerContatos;

    public ContatosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerContatos);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerContatos);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);
        contatos = new ArrayList<>();

        listView = (ListView) view.findViewById(R.id.lv_contatos);

        //adapter = new ArrayAdapter(getActivity(), R.layout.lista_contato, contatos);
        adapter = new ContatoAdapter(getActivity(), contatos);


        listView.setAdapter(adapter);


        //Recuperar Contato do Firebase
        Preferencias preferencias = new Preferencias(getActivity());
        String identificadorUserLogado = preferencias.getIdentificador();
        firebase = ConfiguracaoFirebase.getFirebase().child("Contatos").child(identificadorUserLogado);

        //Listener para recuperar contatos
        valueEventListenerContatos = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Limpar lista
                contatos.clear();

                //Lista contatos
                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Contato contato = dados.getValue( Contato.class );
                    //contatos.add(contato.getNome());
                    contatos.add(contato);

                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ConversaActivity.class);

                //recuperar dados a serem passados
                Contato contato = contatos.get(i);

                //Enviando dados para conversa activity
                intent.putExtra("nome", contato.getNome());
                intent.putExtra("email", contato.getEmail());

                startActivity(intent);
            }
        });

        return view;
    }

}
