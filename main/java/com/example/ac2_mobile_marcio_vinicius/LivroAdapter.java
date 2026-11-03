package com.example.ac2_mobile_marcio_vinicius;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LivroAdapter extends RecyclerView.Adapter<LivroAdapter.ViewHolder> {

    private final List<Livro> livros;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public interface OnItemClickListener {
        void onItemClick(Livro livro);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Livro livro);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public LivroAdapter(List<Livro> livros) {
        this.livros = livros;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        Livro livro = livros.get(pos);

        holder.txt1.setText(livro.getTitulo() + " (" + livro.getAnoDePublicacao() + ")");

        String favorito = livro.isFavorito() ? "Favorito" : "Não favoritado";
        holder.txt2.setText(livro.getAutor() + " • " + livro.getGenero()
                + " • Status: " + livro.getStatusDaLeitura()
                + " • " + favorito);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onItemClick(livro);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) longClickListener.onItemLongClick(livro);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return livros.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt1, txt2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt1 = itemView.findViewById(android.R.id.text1);
            txt2 = itemView.findViewById(android.R.id.text2);
        }
    }
}