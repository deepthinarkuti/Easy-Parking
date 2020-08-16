package siokouros.filippos.phonepark.Main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import siokouros.filippos.phonepark.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ParkingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ParkingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParkingFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    FragmentManager manager;
    Button parkMyCarButton;
    TextView timeRemainingTextView, locationTextView, regNumberTextView, visibleTextView, cancelTextView, extendTextView;
    CardView cardView;

    //firebase Shit
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    String userID;
    DatabaseReference parkingDatabaseReference;


    final String[] test = new String[1];


    private OnFragmentInteractionListener mListener;

    public ParkingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * *
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ParkingFragment.
     */
    public static ParkingFragment newInstance(String param1, String param2) {
        ParkingFragment fragment = new ParkingFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_parking, container, false);
        manager = getFragmentManager();
        final ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setVisibility(View.VISIBLE);

        parkMyCarButton = rootView.findViewById(R.id.park_my_car_button);
        timeRemainingTextView = rootView.findViewById(R.id.time_remaining_text_view);
        locationTextView = rootView.findViewById(R.id.location_text_view);
        regNumberTextView = rootView.findViewById(R.id.reg_number_text_view);
        visibleTextView = rootView.findViewById(R.id.visible_textView);
        cardView = rootView.findViewById(R.id.card_view_parking_fragment);
        visibleTextView = rootView.findViewById(R.id.visible_textView);
        cancelTextView = rootView.findViewById(R.id.cancel_textView);
        extendTextView = rootView.findViewById(R.id.extend_textView);

        cardView.setVisibility(View.GONE);
        visibleTextView.setVisibility(View.INVISIBLE);
        parkMyCarButton.setVisibility(View.VISIBLE);
        extendTextView.setVisibility(View.INVISIBLE);
        cancelTextView.setVisibility(View.INVISIBLE);

        auth = auth = FirebaseAuth.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        parkingDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Parkings");


        parkingDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (Objects.equals(ds.child("completed").getValue(String.class), "false")) {
                        progressBar.setVisibility(View.GONE);
                        test[0] = ds.getKey().toString();
                        fillCardView(ds);
                        cardView.setVisibility(View.VISIBLE);
                        parkMyCarButton.setVisibility(View.GONE);
                        visibleTextView.setVisibility(View.INVISIBLE);
                        extendTextView.setVisibility(View.VISIBLE);
                        cancelTextView.setVisibility(View.VISIBLE);
                        extendTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ExtendFragment extendFragment = new ExtendFragment();
                                final Bundle bundle = new Bundle();
                                bundle.putString("Car",regNumberTextView.getText().toString().trim());
                                extendFragment.setArguments(bundle);
                                manager.beginTransaction()
                                        .replace(R.id.main_frame_fragment, extendFragment).addToBackStack(null)
                                        .commit();
                            }
                        });
                        cancelTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                                } else {
                                    builder = new AlertDialog.Builder(getContext());
                                }
                                builder.setTitle("Cancel parking early")
                                        .setMessage("Are you ready to leave?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Map<String, Object> taskMap = new HashMap<String, Object>();
                                                taskMap.put("completed", "true");
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Parkings").child(test[0]).updateChildren(taskMap);
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // do nothing
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();


                            }
                        });
                    } else if (Objects.equals(ds.child("completed").getValue(String.class), "true")) {
                        progressBar.setVisibility(View.GONE);
                        test[0] = ds.getKey().toString();
                        cardView.setVisibility(View.VISIBLE);
                        parkMyCarButton.setVisibility(View.GONE);
                        visibleTextView.setVisibility(View.VISIBLE);
                        extendTextView.setVisibility(View.INVISIBLE);
                        cancelTextView.setVisibility(View.INVISIBLE);
                        visibleTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Map<String, Object> taskMap = new HashMap<String, Object>();
                                taskMap.put("completed", "dismissed");
                                FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Parkings").child(test[0]).updateChildren(taskMap);
                            }
                        });
                    } else if (Objects.equals(ds.child("completed").getValue(String.class), "dismissed")) {
                        progressBar.setVisibility(View.GONE);
                        test[0] = ds.getKey().toString();
                        cardView.setVisibility(View.GONE);
                        visibleTextView.setVisibility(View.INVISIBLE);
                        parkMyCarButton.setVisibility(View.VISIBLE);
                        extendTextView.setVisibility(View.INVISIBLE);
                        cancelTextView.setVisibility(View.INVISIBLE);
                        Map<String, Object> taskMap = new HashMap<String, Object>();
                        taskMap.put("completed", "finished");
                        FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Parkings").child(test[0]).updateChildren(taskMap);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        parkMyCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParkMyCarFragment parkMyCarFragment = new ParkMyCarFragment();
                manager.beginTransaction()
                        .replace(R.id.main_frame_fragment, parkMyCarFragment).addToBackStack(null)
                        .commit();
            }
        });

        return rootView;
    }


//    private String parkingKey() {
//        final String[] test = new String[1];
//        parkingDatabaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    if(ds.child("completed").equals("false")){
//                        test[0] = ds.getKey().toString();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        return test[0];
//    }

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void fillCardView(DataSnapshot ds) {


            Date currentTime = Calendar.getInstance().getTime();
            String hours = ds.child("hours").getValue().toString();
            String mins = ds.child("minutes").getValue().toString();
            String location = ds.child("location").getValue().toString();
            String regNumber = ds.child("regNumber").getValue().toString();
            String millis = ds.child("endTimeMilis").getValue().toString();
            long millisLong= Long.valueOf(millis);
            Calendar calendar =Calendar.getInstance();
            long currentMillis = calendar.getTimeInMillis();

            locationTextView.setText(location);
            regNumberTextView.setText(regNumber);





               new CountDownTimer(millisLong - currentMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String text = String.format(Locale.getDefault(), "%2d hrs %02d min %02d sec",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                timeRemainingTextView.setText(text);
            }

            @Override
            public void onFinish() {
                Map<String, Object> taskMap = new HashMap<String, Object>();
                taskMap.put("completed", "true");
                FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Parkings").child(test[0]).updateChildren(taskMap);

            }
        }.start();

    }

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
        void onFragmentInteraction(Uri uri);
    }
}
