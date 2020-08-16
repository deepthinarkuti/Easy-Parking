package siokouros.filippos.phonepark.Main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import siokouros.filippos.phonepark.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ParkMyCarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ParkMyCarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParkMyCarFragment extends Fragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ImageButton addHourButton, addMinButtor, subHourButton, subMinButton;
    Button continueButton;
    EditText hourEditText, minEditText;
    Spinner carsSpinner;
    TextView locationTextView;


    //Firebase shit
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String userID;


    FragmentManager manager;
    private double latitude;
    private double longitude;
    private String parkingLocation;


    public ParkMyCarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ParkMyCarFragment.
     */
    public static ParkMyCarFragment newInstance(String param1, String param2) {
        ParkMyCarFragment fragment = new ParkMyCarFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_park_my_car, container, false);

        manager = getFragmentManager();


        addHourButton = rootView.findViewById(R.id.add_hour_button);
        addMinButtor = rootView.findViewById(R.id.add_min_button);
        subHourButton = rootView.findViewById(R.id.sub_hour_button);
        subMinButton = rootView.findViewById(R.id.sub_min_button);
        hourEditText = rootView.findViewById(R.id.hour_edit_text);
        minEditText = rootView.findViewById(R.id.min_edit_text);
        carsSpinner = rootView.findViewById(R.id.cars_spinner);
        continueButton = rootView.findViewById(R.id.continue_button_park_my_car);
        locationTextView = rootView.findViewById(R.id.location_text_view_park_my_car);

        addHourButton.setOnClickListener(this);
        addMinButtor.setOnClickListener(this);
        subHourButton.setOnClickListener(this);
        subMinButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);

        getLocation();



        //Firebase shit
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = firebaseDatabase.getReference("Users").child(userID);


        databaseReference.child("Cars").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> cars = new ArrayList<String>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String car = ds.child("regNumber").getValue(String.class) + " " + ds.child("make").getValue(String.class)+ " " + ds.child("model").getValue(String.class);
                    cars.add(car);
                }

                ArrayAdapter<String> carsAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, cars);
                carsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carsSpinner.setAdapter(carsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return rootView;
    }

    private void getLocation(){
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        locationManager.getBestProvider(criteria, true);
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
        try {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } catch (Exception e) {
            latitude = 0.0;
            longitude = 0.0;
            e.printStackTrace();
        }
        List<Address> addresses;
        locationTextView.setText("Location could not be found");
        if (latitude == 0.0 && longitude == 0.0) {
            // toast
        } else {
            try {
                addresses = gcd.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    parkingLocation = addresses.get(0).getAddressLine(0);
                    locationTextView.setText(addresses.get(0).getAddressLine(0));

                }

            } catch (IOException e) {
                e.printStackTrace();

            }
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

    int hour;
    int min;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_hour_button:
                hour = Integer.parseInt((hourEditText.getText().toString().trim()));
                if (hour == 23) {
                    hourEditText.setText((String.valueOf(0)));
                    break;
                }
                hour++;
                hourEditText.setText((String.valueOf(hour)));
                break;
            case R.id.sub_hour_button:
                hour = Integer.parseInt((hourEditText.getText().toString().trim()));
                if (hour == 0) {
                    hourEditText.setText((String.valueOf(23)));
                    break;
                }
                hour--;
                hourEditText.setText((String.valueOf(hour)));
                break;
            case R.id.add_min_button:
                min = Integer.parseInt((minEditText.getText().toString().trim()));
                if (min == 55) {
                    minEditText.setText(("00"));
                    break;
                }
                min += 5;
                minEditText.setText((String.valueOf(min)));
                break;
            case R.id.sub_min_button:
                min = Integer.parseInt((minEditText.getText().toString().trim()));
                if (min == 0) {
                    minEditText.setText((String.valueOf(55)));
                    break;
                }
                min -= 5;
                minEditText.setText((String.valueOf(min)));
                break;
            case R.id.continue_button_park_my_car:
                if(Integer.parseInt(hourEditText.getText().toString().trim()) == 00 && Integer.parseInt(minEditText.getText().toString().trim()) == 0 ){
                    minEditText.setError("Time can't be 0:00");
                    minEditText.requestFocus();
                    break;
                }
                PaymentFragment paymentFragment = new PaymentFragment();
                final Bundle bundle = new Bundle();
                bundle.putString("Car",carsSpinner.getSelectedItem().toString());
                bundle.putString("hours",hourEditText.getText().toString().trim());
                bundle.putString("mins", minEditText.getText().toString().trim());
                paymentFragment.setArguments(bundle);
                manager.beginTransaction()
                        .replace(R.id.main_frame_fragment, paymentFragment).addToBackStack(null)
                        .commit();
        }
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
