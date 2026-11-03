package com.example.ac2_mobile_marcio_vinicius;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText editTextTitulo, editTextAutor, editTextAno, editTextBusca;
    private RecyclerView recycler;
    private final List<Livro> listaCompleta = new ArrayList<>();
    private final List<Livro> listaLivros = new ArrayList<>();
    private LivroAdapter adapter;
    private Spinner spinnerGenero, spinnerStatus;
    private CheckBox checkBoxFavorito;
    private Button button;
    private Livro livroEditando = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        editTextTitulo = findViewById(R.id.editTextTitulo);
        editTextAutor = findViewById(R.id.editTextAutor);
        editTextAno = findViewById(R.id.editTextAnoDePublicacao);
        editTextBusca = findViewById(R.id.editTextBusca);
        recycler = findViewById(R.id.recycler);
        spinnerGenero = findViewById(R.id.spinner);
        spinnerStatus = findViewById(R.id.spinner2);
        checkBoxFavorito = findViewById(R.id.checkBox);
        button = findViewById(R.id.button);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LivroAdapter(listaLivros);
        recycler.setAdapter(adapter);

        String[] generos = {"Selecione o gênero", "Ficção cientifica", "Fantasia", "Romance", "Terror",
                "Biografia"};
        String[] status = {"Selecione o status", "Quero ler", "Lendo", "Lido" };
        spinnerGenero.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, generos));
        spinnerStatus.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, status));

        adapter.setOnItemClickListener(livro -> {
            editTextTitulo.setText(livro.getTitulo());
            editTextAutor.setText(livro.getAutor());
            editTextAno.setText(livro.getAnoDePublicacao());
            selecionarSpinner(spinnerGenero, livro.getGenero());
            selecionarSpinner(spinnerStatus, livro.getStatusDaLeitura());
            checkBoxFavorito.setChecked(livro.isFavorito());
            livroEditando = livro;
            button.setText("Atualizar");
        });

        adapter.setOnItemLongClickListener(this::confirmarExclusao);

        editTextBusca.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                aplicarFiltro();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        button.setOnClickListener(v -> salvarLivro());

        carregarLivros();
    }

    private void salvarLivro() {
        String titulo = editTextTitulo.getText().toString().trim();
        String autor = editTextAutor.getText().toString().trim();
        String ano = editTextAno.getText().toString().trim();
        boolean favorito = checkBoxFavorito.isChecked();

        if (titulo.isEmpty()) {
            editTextTitulo.setError("Informe o título");
            editTextTitulo.requestFocus();
            return;
        }
        if (spinnerGenero.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione o gênero", Toast.LENGTH_SHORT).show();
            return;
        }
        if (spinnerStatus.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione o status da leitura", Toast.LENGTH_SHORT).show();
            return;
        }
        if (autor.isEmpty()) {
            editTextAutor.setError("Informe o autor");
            editTextAutor.requestFocus();
            return;
        }
        if (ano.isEmpty()) {
            editTextAno.setError("Informe o ano de publicação");
            editTextAno.requestFocus();
            return;
        }

        String genero = spinnerGenero.getSelectedItem().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        if (livroEditando == null) {
            Livro livro = new Livro(null, titulo, ano, genero, status, favorito, autor);
            db.collection("livros")
                    .add(livro)
                    .addOnSuccessListener(doc -> {
                        Toast.makeText(this, "Livro salvo!", Toast.LENGTH_SHORT).show();
                        limparFormulario();
                        carregarLivros();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erro ao salvar", Toast.LENGTH_SHORT).show());
        } else {
            livroEditando.setTitulo(titulo);
            livroEditando.setAutor(autor);
            livroEditando.setAnoDePublicacao(ano);
            livroEditando.setGenero(genero);
            livroEditando.setStatusDaLeitura(status);
            livroEditando.setFavorito(favorito);

            db.collection("livros").document(livroEditando.getId())
                    .set(livroEditando)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Livro atualizado!", Toast.LENGTH_SHORT).show();
                        limparFormulario();
                        carregarLivros();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erro ao atualizar", Toast.LENGTH_SHORT).show());
        }
    }

    private void confirmarExclusao(Livro livro) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir")
                .setMessage("Deseja excluir \"" + livro.getTitulo() + "\"?")
                .setPositiveButton("Excluir", (d, w) -> deletarLivro(livro))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deletarLivro(Livro livro) {
        if (livro.getId() == null) return;
        db.collection("livros").document(livro.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (livroEditando != null && livro.getId().equals(livroEditando.getId())) {
                        limparFormulario();
                    }
                    Toast.makeText(this, "Livro excluído!", Toast.LENGTH_SHORT).show();
                    carregarLivros();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao excluir", Toast.LENGTH_SHORT).show());
    }

    private void carregarLivros() {
        db.collection("livros")
                .get()
                .addOnSuccessListener(query -> {
                    listaCompleta.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        Livro l = doc.toObject(Livro.class);
                        l.setId(doc.getId());
                        listaCompleta.add(l);
                    }
                    aplicarFiltro();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao carregar livros", Toast.LENGTH_SHORT).show());
    }

    private void aplicarFiltro() {
        String busca = editTextBusca.getText().toString().trim().toLowerCase();
        listaLivros.clear();
        for (Livro l : listaCompleta) {
            boolean achouTitulo = l.getTitulo() != null
                    && l.getTitulo().toLowerCase().contains(busca);
            boolean achouAutor = l.getAutor() != null
                    && l.getAutor().toLowerCase().contains(busca);
            if (busca.isEmpty() || achouTitulo || achouAutor) {
                listaLivros.add(l);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void limparFormulario() {
        editTextTitulo.setText("");
        editTextAutor.setText("");
        editTextAno.setText("");
        spinnerGenero.setSelection(0);
        spinnerStatus.setSelection(0);
        checkBoxFavorito.setChecked(false);
        livroEditando = null;
        button.setText("Salvar");
    }

    private void selecionarSpinner(Spinner spinner, String valor) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<String> a = (ArrayAdapter<String>) spinner.getAdapter();
        int pos = valor == null ? -1 : a.getPosition(valor);
        spinner.setSelection(pos >= 0 ? pos : 0);
    }
}