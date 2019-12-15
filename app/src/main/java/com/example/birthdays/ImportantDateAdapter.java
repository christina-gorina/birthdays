package com.example.birthdays;

import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ImportantDateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context cnt;
    ArrayList<ImportantDatesItem> importantDateItems;
    private EventsDbHelper eventsDbHelper;

    public ImportantDateAdapter(ArrayList<ImportantDatesItem> importantDateItems , Context context) {
        this.cnt = context;
        this.importantDateItems = importantDateItems;
    }

    public static class ImportantDateHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView data;
        Button delete;

        public ImportantDateHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            data = itemView.findViewById(R.id.data);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.importantdate_item, viewGroup, false);
        return new ImportantDateHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        ImportantDatesItem item = importantDateItems.get(position);
        ImportantDateHolder eventHolder = (ImportantDateHolder) viewHolder;
        eventHolder.name.setText(item.getName());
        eventHolder.data.setText(item.getData());

        final int index = position;
        final int id = item.getId();

        eventHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("item.getId() = " + id);

                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                alertDialog.setTitle("Удаление");
                alertDialog.setMessage("Вы хотите удалить запись?");

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Нет",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Да",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                eventsDbHelper = new EventsDbHelper(cnt);
                                boolean isDeleted = eventsDbHelper.onDelete(id);
                                System.out.println("isDeleted = " + isDeleted);
                                if(isDeleted) {
                                    try {
                                        importantDateItems.remove(index);
                                        notifyItemRemoved(index);
                                        notifyItemRangeChanged(index, getItemCount());
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                                eventsDbHelper.close();
                                dialog.dismiss();
                            }
                        });

                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return importantDateItems.size();
    }
}
