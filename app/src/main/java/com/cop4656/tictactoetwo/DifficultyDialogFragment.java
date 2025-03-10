package com.cop4656.tictactoetwo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DifficultyDialogFragment extends DialogFragment {

    public interface DifficultySelectListener{
        void onDifficultySelected(String diff);
    }

    private DifficultySelectListener listener;


    public DifficultyDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_difficulty_dialog, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_difficulty)
                .setItems(R.array.difficulty_levels, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] difficulties = getResources().getStringArray(R.array.difficulty_levels);
                        listener.onDifficultySelected(difficulties[which]);
                    }
                });
        return builder.create();
    }

    public void setDifficultySelectListener(DifficultySelectListener listener){
        this.listener = listener;
    }
}