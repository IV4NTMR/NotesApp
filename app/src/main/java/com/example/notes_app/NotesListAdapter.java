package com.example.notes_app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.ViewHolder> {
    private List<NoteElement> data;
    private LayoutInflater inflater;
    private Context context;

    ImageView icon;
    TextView title, date;
    private int position;

    final NotesListAdapter.OnItemClickListener listener;


    public interface OnItemClickListener{
        void onItemClick(NoteElement item);
        void onLongItemClick(NoteElement item);
    }

    public NotesListAdapter(List<NoteElement> itemList, Context context, NotesListAdapter.OnItemClickListener listener){
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = itemList;
        this.listener = listener;
    }

    @Override
    public int getItemCount(){
        return data.size();
    }

    @Override
    public NotesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = inflater.inflate(R.layout.notes_rv_element, parent, false);
        return new NotesListAdapter.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(final NotesListAdapter.ViewHolder holder, final int position){
        holder.bindData(data.get(position));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getAbsoluteAdapterPosition());
                return false;
            }
        });
    }

    public void setItems(List<NoteElement> items){ data = items; }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{


        ViewHolder (View itemView){
            super(itemView);
            icon = itemView.findViewById(R.id.note_type_icon);
            title = itemView.findViewById(R.id.note_title_TxtView);
            date = itemView.findViewById(R.id.note_date);
            itemView.setOnCreateContextMenuListener(this);

        }

        void bindData(final NoteElement item){
            //icon.setImageResource(); --> Encuentra la manera de hacer que cambie el icono dinámicamente
            title.setText(item.getNoteTitle());
            date.setText(item.getDate());

            dinamicThemeDataBinding(item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongItemClick(item);
                    return true;}
            });


        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(menu.NONE, 1, 1, "Editar Nota");
            menu.add(menu.NONE, 2, 2, "Info de la Nota");
            menu.add(menu.NONE, 3, 3, "Ocultar Nota");
            menu.add(menu.NONE, 4, 4, "Eliminar Nota");
        }

    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public NoteElement getNoteElement(int position){
        return data.get(position);
    }


   @Override
    public void onViewRecycled(ViewHolder holder){
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    public void dinamicThemeDataBinding(NoteElement noteItem){
        //En esta parte cambiamos los colores y el icoono de cata item de tipo nota dependiendo del tipo de nota

    }
}
