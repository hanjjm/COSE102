package com.example.hanju.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import static com.example.hanju.myapplication.MainActivity.mDbOpenHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Tab3.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Tab3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tab3 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Tab3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tab3.
     */
    // TODO: Rename and change types and number of parameters
    public static Tab3 newInstance(String param1, String param2) {
        Tab3 fragment = new Tab3();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab3, container, false);
        final ListView listview = view.findViewById(R.id.favoritelist);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /// deleted from favorite list
            //    Toast.makeText(getContext(), "A person deleted from the favorite list", Toast.LENGTH_LONG).show();
                Tab3ListAdapter.ViewHolder holder = (Tab3ListAdapter.ViewHolder) view.getTag();

                //database update
                String phoneNum = (String) holder.phoneNum.getText();
                Cursor cursor = mDbOpenHelper.mDB.rawQuery("SELECT * FROM people", null);
                while (cursor.moveToNext()) {
                    int ID = cursor.getInt(0);
                    String NAME = cursor.getString(1);
                    String PHONE =  cursor.getString(2);

                    int FAVOR = cursor.getInt(3);
                    if(PHONE.equals(phoneNum)){
                        mDbOpenHelper.updateColumn(ID,NAME,PHONE,0);
                    }
                }
                ListUpdate(listview);
            }
        });

        Button camera = (Button) view.findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Favorite list 저장!", Toast.LENGTH_LONG).show();
                View rootView = getActivity().getWindow().getDecorView();
                File screenShot =  ScreenShot(rootView);
                if(screenShot!=null){
                    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
                }
            }
        });

        ListUpdate(listview);

        Button btn = (Button) view.findViewById(R.id.favoritebutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Toast.makeText(getContext(), "~~~~~~~", Toast.LENGTH_SHORT);
                Intent i = new Intent(getContext(), FavoritFriend.class);
                startActivity(i);
            }
        });

        Button updatebtn = (Button) view.findViewById(R.id.updatebtn);
        updatebtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ListUpdate(listview);
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void ListUpdate(ListView l1){

        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<String> phoneNumList = new ArrayList<>();

        if (  mDbOpenHelper.mDB != null) {
            //CONTACTS.execSQL("CREATE TABLE IF NOT EXISTS people (Name TEXT, Phonenumber TEXT, Favor INTEGER)");
            // 쿼리문으로 데이터 불러옴
            Cursor cursor = mDbOpenHelper.mDB.rawQuery("SELECT * FROM people", null);
            while (cursor.moveToNext()) {
                String name = cursor.getString(1);
                String phoneNum = cursor.getString(2);
                int favor = cursor.getInt(3);
                if(favor == 1){
                    nameList.add(name);
                    phoneNumList.add(phoneNum);
                }
            }
        }
        Tab3ListAdapter adapter = new Tab3ListAdapter (getContext(),R.layout.item_list3, nameList, phoneNumList);
        l1.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        ListView listview = getActivity().findViewById(R.id.favoritelist);
        ListUpdate(listview);
    }

    public File ScreenShot(View view) {
        view.setDrawingCacheEnabled(true);

        Bitmap screenBitmap = view.getDrawingCache();

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");

        String formatDate = sdfNow.format(date);
        String filename = formatDate+".png";
        File file = new File(Environment.getExternalStorageDirectory()+"/Pictures" , filename);

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        view.setDrawingCacheEnabled(false);
        return file;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
