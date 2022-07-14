package com.pl.donauturm.drinksmenu.view.preferences.font;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.model.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Kizito Nwose on 9/28/2016.
 */
public class FontDialog extends DialogFragment implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {
    private OnFontSelectedListener fontSelectedListener;

    private FontPreference preference;
    private ListView fontListView;

    public FontDialog() {
    }

    public static FontDialog newInstance(FontPreference preference) {
        FontDialog dialog = new FontDialog();
        dialog.preference = preference;
        return dialog;
    }

    public void setOnColorSelectedListener(OnFontSelectedListener fontSelectedListener) {
        this.fontSelectedListener = fontSelectedListener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFontSelectedListener) {
            setOnColorSelectedListener((OnFontSelectedListener) context);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View rootView = layoutInflater.inflate(R.layout.dialog_select_font, null);

        Font font = preference.getValue();
        List<Font> fonts = preference.getFonts();
        fontListView = rootView.findViewById(R.id.font_list_view);
        fontListView.setAdapter(new FontAdapter(fonts, font));
        fontListView.setOnItemClickListener(this);
        SearchView fontSearch = rootView.findViewById(R.id.font_search);
        fontSearch.setQuery("", false);
        fontSearch.setOnQueryTextListener(this);

        return new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setTitle("Choose Font")
                .setNegativeButton("ABBRECHEN", null)
                .create();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Font selectedFont = ((Font) fontListView.getAdapter().getItem(position));
        if (fontSelectedListener != null)
            fontSelectedListener.onFontSelected(selectedFont);
        dismiss();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        FontAdapter fontAdapter = ((FontAdapter) fontListView.getAdapter());
        fontAdapter.filter(query);
        fontAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        FontAdapter fontAdapter = ((FontAdapter) fontListView.getAdapter());
        fontAdapter.filter(newText);
        fontAdapter.notifyDataSetChanged();
        return false;
    }

    public interface OnFontSelectedListener {
        void onFontSelected(Font font);
    }

    // Font adaptor responsible for redrawing the item TextView with the appropriate font.
    // We use BaseAdapter since we need both arrays, and the effort is quite small.
    public class FontAdapter extends BaseAdapter {

        private final List<Font> fonts;
        private List<Font> filteredFonts;
        private final Font selected;

        public FontAdapter(List<Font> fonts, Font selected) {
            this.fonts = new ArrayList<>(fonts);
            this.filteredFonts = new ArrayList<>(fonts);
            this.selected = selected;
        }

        public void filter(String text){
            filteredFonts = fonts.stream().filter(f -> f.getFontNameFast().toLowerCase().contains(text.toLowerCase())).collect(Collectors.toList());
        }

        @Override
        public int getCount() {
            return filteredFonts.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredFonts.get(position);
        }

        @Override
        public long getItemId(int position) {
            // We use the position as ID
            return fonts.indexOf(filteredFonts.get(position));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            // This function may be called in two cases: a new view needs to be created,
            // or an existing view needs to be reused
            if (view == null) {
                // Since we're using the system list for the layout, use the system inflater
                final LayoutInflater inflater = (LayoutInflater)
                        requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // And inflate the view android.R.layout.select_dialog_singlechoice
                // Why? See com.android.internal.app.AlertController method createListView()
                view = inflater.inflate(android.R.layout.select_dialog_singlechoice, parent, false);
            }

            if (view != null) {
                // Find the text view from our interface
                CheckedTextView tv = view.findViewById(android.R.id.text1);

                // Replace the string with the current font name using our typeface
                Typeface typeface = filteredFonts.get(position).getTypeFace();
                if (typeface != null)
                    tv.setTypeface(typeface);
                else {
                    tv.setClickable(false);
                    tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }

                tv.setText(filteredFonts.get(position).getFontName());

                tv.setChecked(filteredFonts.get(position).equals(selected));
            }

            return view;
        }
    }
}
